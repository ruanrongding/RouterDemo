package com.example.commonlibs;

import com.alibaba.android.arouter.launcher.ARouter;

public class AppConfig {
    private static final String LoginApp = "com.example.login.LoginApplication";
    private static final String ShareApp = "com.example.login.LoginApplication";
    private static final String MainApp = "com.example.login.LoginApplication";

    public static String[] moduleApps = {
            LoginApp,ShareApp,MainApp
    };

}
