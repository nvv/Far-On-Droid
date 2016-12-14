package com.openfarmanager.android_dev;

import android.support.multidex.MultiDex;

import com.openfarmanager.android.App;

/**
 * @author Vlad Namashko
 */
public class DevApp extends App {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }

}
