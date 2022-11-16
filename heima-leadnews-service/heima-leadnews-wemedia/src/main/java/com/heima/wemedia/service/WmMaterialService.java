package com.heima.wemedia.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialService {

    //图片上传
    ResponseResult uploadPicture(MultipartFile multipartFile);

    //素材列表查询
    ResponseResult findList(WmMaterialDto dto);
}
