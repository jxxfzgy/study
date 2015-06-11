package com.gyzhong.toolbar;

import android.app.Application;

import com.crashhandler.CrashHandler;

/**
 * Created by moon.zhong on 2015/6/11.
 * time : 10:27
 */
public class Applica extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance() ;
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
    }
}
