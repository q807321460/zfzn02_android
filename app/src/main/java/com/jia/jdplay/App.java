package com.jia.jdplay;

import android.app.Application;
import android.util.Log;

import com.judian.support.jdplay.sdk.JdPlayManager;

public class App extends Application {

    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        JdPlayImageUtils.getInstance().initialize(this);
        JdPlayManager.getInstance().initialize(this);
        Log.v(TAG, "JdPlayManager version : " + JdPlayManager.getInstance().getVersion());
    }

}

