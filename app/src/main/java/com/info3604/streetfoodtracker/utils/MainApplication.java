package com.info3604.streetfoodtracker.utils;

import android.app.Application;

import com.onesignal.OneSignal;

public class MainApplication extends Application {

    private static final String ONESIGNAL_APP_ID = "38948f29-46b7-450b-b1ee-56ca9b2c9ddf";

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
    }
}
