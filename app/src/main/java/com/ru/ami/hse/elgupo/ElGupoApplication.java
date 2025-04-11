package com.ru.ami.hse.elgupo;

import android.app.Application;
import android.content.Context;

public class ElGupoApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        ElGupoApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }
}
