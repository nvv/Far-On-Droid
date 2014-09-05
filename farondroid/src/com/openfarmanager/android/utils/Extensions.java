package com.openfarmanager.android.utils;

import com.openfarmanager.android.App;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Extensions {

    private static ExecutorService sService = Executors.newFixedThreadPool(1);

    public static void runAsynk(Callable callable) {
        //noinspection unchecked
        sService.submit(callable);
    }

    public static void runAsynk(Runnable runnable) {
        //noinspection unchecked
        sService.submit(runnable);
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
