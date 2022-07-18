package com.example.commonlibs;

import android.app.Application;
import android.util.Log;

public class BaseApplication extends Application {



    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG","BaseApplication onCreate()");
    }



    public void initArouter(){

    }
}
