package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.thread.WmThreadLocal;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsService;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    WmNewsService wmNewsService;
   @Autowired
   private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Override
    public PageResponseResult findAll(WmNewsPageReqDto dto) {

        //1.构建查询条件
        LambdaQueryWrapper<WmNews> wrapper = Wrappers.lambdaQuery(WmNews.class)
                //状态 status;
                .eq(dto.getStatus() != null, WmNews::getStatus, dto.getStatus())
                //开始时间 beginPubDate;
                //结束时间 endPubDate;
                .between(dto.getBeginPubDate() != null && dto.getEndPubDate() != null,
                        WmNews::getPublishTime, dto.getBeginPubDate(), dto.getEndPubDate())
                //所属频道ID channelId;
                .eq(dto.getChannelId() != null, WmNews::getChannelId, dto.getChannelId())
                //关键字 keyword;
                .like(StringUtils.isNotBlank(dto.getKeyword()), WmNews::getTitle, dto.getKeyword())
                //当前用户id
                .eq(WmNews::getUserId, WmThreadLocal.getUser().getId())
                //发布时间倒序
                .orderBy(true, false, WmNews::getPublishTime);

        //2.分页查询
        Page<WmNews> page = new Page<>(dto.getPage(), dto.getSize());
        page = page(page,wrapper);
        PageResponseResult pageResponseResult = new PageResponseResult((int) page.getCurrent(), (int) page.getSize(), (int) page.getTotal());
        pageResponseResult.setData(page.getRecords());
        return pageResponseResult;
    }

    @Override
    public ResponseResult submitNews(WmNewsDto dto) {
        //1 根据dto准备文章数据
        if (dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        WmNews wmNews = new WmNews();
        //复制对象的属性
        BeanUtils.copyProperties(dto,wmNews);
        wmNews.setUserId(WmThreadLocal.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        //默认上架  0下架   1上架
        wmNews.setEnable(1);
        //保存封面图片
        wmNews.setImages(StringUtils.join(dto.getImages(), ","));

        //2判断id是
        // 否存在
        if (dto.getId() == null){
            //2.1不存在ID 则新增
            this.save(wmNews);
        }else {
            //2.2存在，则删除文章和素材的关系
            wmNewsMaterialMapper.delete(Wrappers.lambdaQuery(WmNewsMaterial.class)
                    .eq(WmNewsMaterial::getNewsId, dto.getId()));

            //修改文章
            this.updateById(wmNews);
        }
        //3 判断是否为草稿
        // status   状态 草稿为0   提交为1
        if (dto.getStatus() == 0 ){
            return ResponseResult.okResult(null);
        }

        //4 如果不是草稿，处理 文章内容和素材的关系 文章封面的图片和素材的关系

        //4.1 处理文章内容  的   文章和图片素材的关系
        //创建容器，存放文章内容中的素材url地址
        List<String> contentImages = new ArrayList<>();

        //判断内容是否存在
        if (StringUtils.isNotBlank(dto.getContent())){
            List<Map> maps = JSONArray.parseArray(dto.getContent(), Map.class);
            for (Map map : maps) {

                //    "type": "image",
                //    "value": "http://192.168.200.128:9000/leadnews/2022/05/19/8bbffbb3d336dbac7.jpg"

                if (map.get("type").equals("image")){
                    contentImages.add(map.get("value").toString());
                }
            }
        }

        //调用方法保存 文章内容的 文章和图片素材的关系
        saveWmNewsMaterial(contentImages, wmNews.getId(), 0);

        return null;
    }

    /**
     * 批量保存  自媒体文章  和  素材的关系
     *
     * @param imageList 图片的地址  用来反查素材id
     * @param wmNewsId  自媒体文章id
     * @param type      引用类型 0 内容引用    1 主图引用
     */
    private void saveWmNewsMaterial(List<String> imageList, Integer wmNewsId, int type) {

        //1.判断数据是否为空
        if (CollectionUtils.isEmpty(imageList)) {
            return;
        }

        //2.遍历imageList，通过Url反查数据库图片id
        List<Integer> imgIdList = new ArrayList<>();
        for (String url : imageList) {
            Integer id = wmMaterialMapper.selectOne(Wrappers.lambdaQuery(WmMaterial.class).eq(WmMaterial::getUrl, url)).getId();
            imgIdList.add(id);
        }

        //3. 遍历素材id  批量保存关系数据
        List<WmNewsMaterial> wmNewsMaterials = new ArrayList<>();
        for (Integer id : imgIdList) {
            WmNewsMaterial wmNewsMaterial = new WmNewsMaterial();
            wmNewsMaterial.setMaterialId(id);
            wmNewsMaterial.setNewsId(wmNewsId);
            wmNewsMaterial.setType(type);
            wmNewsMaterials.add(wmNewsMaterial);
        }
        wmNewsMaterialMapper.saveBatch(wmNewsMaterials);

    }


}
