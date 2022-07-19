package com.example.commonlibs;

import android.app.Application;
import android.util.Log;

public  abstract class BaseApplication extends Application {

    /**
     * Application 初始化
     */
    public abstract void initModuleApp(Application application);

    /**
     * 所有 Application 初始化后的自定义操作
     */
    public abstract void initModuleData(Application application);
}

