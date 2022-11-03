package com.heima.user.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {

    @Autowired
    ApUserMapper apUserMapper;

    //用户登录
    @Override
    public ResponseResult login(LoginDto dto) {

        String phone = dto.getPhone();
        String password = dto.getPassword();

        //1.正常登录
        if (!StringUtils.isBlank(phone) && !StringUtils.isBlank(password)){
            //1.1查询用户
            ApUser apUser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, phone));
            if (apUser==null){
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户不存在");
            }else {
                //1.2对比密码
                String salt = apUser.getSalt();
                String digest = DigestUtils.md5DigestAsHex((password + salt).getBytes());
                if (!digest.equals(apUser.getPassword())) {
                    return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
                }
                //1.3返回数据 JWT
                Map<String, Object> map = new HashMap<>();
                map.put("token",AppJwtUtil.getToken(apUser.getId().longValue()));
                apUser.setSalt("");
                apUser.setPassword("");
                map.put("user",apUser);
                return ResponseResult.okResult(map);
            }
        }else {
            //游客登陆 同样返回token id=0
            Map<String, Object> map = new HashMap<>();
            map.put("token", AppJwtUtil.getToken(0l));
            return ResponseResult.okResult(map);

        }

    }
}
