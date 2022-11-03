package com.heima.common.thread;

import com.heima.model.wemedia.pojos.WmUser;

public class WmThreadLocal {

    private final static ThreadLocal<WmUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    //添加用户
    public static void setUser(WmUser wmUser){
        WM_USER_THREAD_LOCAL.set(wmUser);
    }

    //获取用户
    public static WmUser getUser(){
        return WM_USER_THREAD_LOCAL.get();
    }

    //删除用户
    public static void clear(){
        WM_USER_THREAD_LOCAL.remove();
    }

}
