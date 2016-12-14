package com.openfarmanager.android.utils;

import com.openfarmanager.android.App;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class Extensions {

    public static Future runAsync(Callable callable) {
        return App.sInstance.getThreadPool().getExecutor().submit(callable);
    }

    public static Future runAsync(Runnable runnable) {
        return App.sInstance.getThreadPool().getExecutor().submit(runnable);
    }

    public static ExecutorService getThreadPool() {
        return App.sInstance.getThreadPool().getExecutor();
    }

    public static int tryParse(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ignored) { }

        return defaultValue;
    }

    public static double tryParse(String string, double defaultValue) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException ignored) { }

        return defaultValue;
    }

    public static boolean tryParse(String string, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(string);
        } catch (NumberFormatException ignored) { }

        return defaultValue;
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().equals("");
    }

    public static int getResourceId(String type, String identifier) {
        return App.sInstance.getResources().getIdentifier(identifier, type, App.sInstance.getPackageName());
    }
}
