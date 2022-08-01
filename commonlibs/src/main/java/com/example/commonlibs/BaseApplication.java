package com.example.commonlibs;

import android.app.Application;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;

public  class BaseApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
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

