package com.example.share;
import android.app.Application;
import android.util.Log;

import com.example.commonlibs.BaseApplication;
/**
 *
 */
public class ShareApplication extends BaseApplication {
    @Override
    public void onCreate() {
        Log.e("TAG","ShareApplication onCreate()");
        super.onCreate();
        initModuleApp(this);
        initModuleData(this);
    }

    @Override
    public void initModuleApp(Application application) {

    }

    @Override
    public void initModuleData(Application application) {

    }
}
