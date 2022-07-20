package com.example.login.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.commonlibs.service.RegisterService;

@Route(path ="/login/doRegister")
public class RegisterServiceImpl implements RegisterService {
    @Override
    public String doRegister(String userName, String passwrod) {
        return userName + passwrod;
    }
    @Override
    public void init(Context context) {

    }
}
