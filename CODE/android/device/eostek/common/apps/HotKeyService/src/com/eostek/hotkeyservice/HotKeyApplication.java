package com.eostek.hotkeyservice;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;

public class HotKeyApplication extends Application implements UncaughtExceptionHandler {

    @Override
    public void onCreate() {
        super.onCreate();
//        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        System.exit(0);
    }

}
