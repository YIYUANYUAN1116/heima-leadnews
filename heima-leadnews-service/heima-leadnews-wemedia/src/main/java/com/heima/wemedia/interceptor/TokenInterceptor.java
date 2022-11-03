package com.heima.wemedia.interceptor;

import com.heima.common.thread.WmThreadLocal;
import com.heima.model.wemedia.pojos.WmUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenInterceptor implements HandlerInterceptor {

    //执行Controller之前  执行该方法
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求头中的用户id
        String userId = request.getHeader("userId");
        if (StringUtils.isNotEmpty(userId)){
            WmUser wmUser = new WmUser();
            wmUser.setApUserId(Integer.valueOf(userId));
            //放到ThreadLocal中
            WmThreadLocal.setUser(wmUser);
        }
        return true;
    }

    //最终执行的方法
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        WmThreadLocal.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
