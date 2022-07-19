package com.example.commonlibs.service;

public class EmptyService  implements  LoginService{
    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public String getPassword() {
        return null;
    }
}
