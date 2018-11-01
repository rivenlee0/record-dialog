package com.example.rivenlee.recorddialog;

import android.app.Application;

import tech.oom.idealrecorder.IdealRecorder;

/**
 * author: rivenlee
 * date: 2018/11/1
 * email: rivenlee0@gmail.com
 */
public class RecodeApp extends Application {
    private static RecodeApp instance;
    public static RecodeApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = RecodeApp.this;
        IdealRecorder.init(RecodeApp.this);

    }
}
