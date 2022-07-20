package com.example.routerdemo;

import android.app.Application;
import android.util.Log;


import com.alibaba.android.arouter.launcher.ARouter;
import com.example.commonlibs.AppConfig;
import com.example.commonlibs.BaseApplication;


public class MainApplication extends BaseApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG","MainApplication onCreate()");
        initARouter();


    }

    /**
     * 初始化ARouter
     */
    private void initARouter() {
        if(BuildConfig.DEBUG){
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }


}


