package com.example.commonlibs.service;

public interface LoginService {
    /**
     *是否登录
     * @return
     */
    boolean isLogin();

    /**
     * 获取登录用户的密码
     * @return
     */
    String getPassword();
}
