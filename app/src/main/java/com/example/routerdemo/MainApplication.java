package com.example.routerdemo;

import android.app.Application;
import android.util.Log;

import com.example.commonlibs.AppConfig;
import com.example.commonlibs.BaseApplication;

public class MainApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG","MainApplication onCreate()");
       initModuleApp(this);
       initModuleData(this);
    }

    @Override
    public void initModuleApp(Application application) {
        for (String moduleApp : AppConfig.moduleApps) {
            try {
                Class clazz = Class.forName(moduleApp);
                BaseApplication baseApp = (BaseApplication) clazz.newInstance();
                baseApp.initModuleApp(this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initModuleData(Application application) {
        for (String moduleApp : AppConfig.moduleApps) {
            try {
                Class clazz = Class.forName(moduleApp);
                BaseApplication baseApp = (BaseApplication) clazz.newInstance();
                baseApp.initModuleData(this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }
}


