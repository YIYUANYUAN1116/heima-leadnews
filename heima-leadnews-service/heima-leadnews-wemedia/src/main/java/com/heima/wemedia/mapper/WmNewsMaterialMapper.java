package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {
    void saveBatch(@Param("list") List<WmNewsMaterial> wmNewsMaterials);
}
