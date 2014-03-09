package com.openfarmanager.android.googledrive;

import android.app.Application;
import android.preference.PreferenceManager;

/**
 * author: Vlad Namashko
 */
public class App extends Application {

    public static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
