package com.heima.wemedia.service;

import com.heima.model.common.dtos.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialService {

    //图片上传
    ResponseResult uploadPicture(MultipartFile multipartFile);
}
