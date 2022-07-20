package com.example.login;
import android.app.Application;
import android.util.Log;

import com.example.commonlibs.BaseApplication;
import com.example.commonlibs.service.ServiceFactory;
import com.example.login.service.AccountService;

/**
 *
 */
public class LoginApplication extends BaseApplication {
    @Override
    public void onCreate() {
        Log.e("TAG","LoginApplication onCreate()");
        super.onCreate();
        ServiceFactory.getInstance().setLoginService(new AccountService(LoginUtils.isLogin,LoginUtils.password));
    }


}
