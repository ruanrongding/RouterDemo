package com.example.commonlibs.service;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface RegisterService extends IProvider {

    String doRegister(String userName, String passwrod);
}
