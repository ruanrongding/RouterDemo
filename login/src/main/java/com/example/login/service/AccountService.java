package com.example.login.service;

import com.example.commonlibs.service.LoginService;

public class AccountService implements LoginService {
    private boolean isLogin;
    private String password;

    public AccountService(boolean isLogin,String password){
        this.isLogin = isLogin;
        this.password = password;
    }
    @Override
    public boolean isLogin() {
        return isLogin;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
