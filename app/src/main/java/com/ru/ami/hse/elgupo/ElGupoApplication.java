package com.ru.ami.hse.elgupo;

import android.app.Application;
import android.content.Context;

import com.yandex.mapkit.MapKitFactory;

public class ElGupoApplication extends Application {
    private static Context context;
    private static boolean isMapKitInitialized = false;

    @Override
    public void onCreate() {
        super.onCreate();
        ElGupoApplication.context = getApplicationContext();
        initializeMapKit();
    }

    public static Context getAppContext() {
        return context;
    }

    private void initializeMapKit() {
        if (!isMapKitInitialized) {
            MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY);
            MapKitFactory.initialize(this);
            isMapKitInitialized = true;
        }
    }
}
