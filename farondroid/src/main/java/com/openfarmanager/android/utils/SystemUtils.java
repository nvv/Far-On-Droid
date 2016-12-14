package com.openfarmanager.android.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.openfarmanager.android.App;

import java.io.File;

/**
 * System utilities methods.
 *
 * @author Vlad Namashko
 */
public class SystemUtils {

    private static boolean sIsBigScreen = true;
    private static boolean sIsTablet = true;

    public static void init(Context context) {
        int sizeCategory = context.getResources().getConfiguration().screenLayout;
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        int screenSize = (sizeCategory & Configuration.SCREENLAYOUT_SIZE_MASK);

        if (screenSize <= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            if (metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                // Weird Samsung device & Kobo & all others 2.x
                sIsBigScreen = false;
                sIsTablet = screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE;
            } else if (screenSize < Configuration.SCREENLAYOUT_SIZE_LARGE) {
                sIsBigScreen = sIsTablet = false;
            } else {
                sIsBigScreen = true;
            }
        }
    }

    public static int getAndroidSdkInt() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * Determines, if current android version is Honeycomb (ask 11) or never.
     * It's important due some incompatibility issues with older versions.
     *
     * @return <code>true</code> if current os version is 11 or never, <code>false</code> otherwise.
     */
    public static boolean isHoneycombOrNever() {
        return getAndroidSdkInt() >= 11;
    }

    public static boolean isBigScreen() {
        return sIsBigScreen;
    }

    public static boolean isTablet() {
        return sIsTablet;
    }

    @TargetApi(21)
    public static String getExternalStorage(final String fullPath) {
        if (StorageUtils.checkVersion()) {
            File[] dirs = App.sInstance.getExternalFilesDirs(null);
            for (File dir : dirs) {
                dir = dir.getParentFile().getParentFile().getParentFile().getParentFile();
                if (fullPath.toLowerCase().startsWith(dir.getAbsolutePath().toLowerCase()) && Environment.isExternalStorageRemovable(dir)) {
                    return dir.getAbsolutePath();
                }
            }
        }
        return null;
    }

}
