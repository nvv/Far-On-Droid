package com.openfarmanager.android.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

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

    public static String getExternalStorage(final String fullPath) {

        Observable<String> sdCardNames = Observable.from(Arrays.asList("ext_card", "external_sd",
                "ext_sd", "external", "extSdCard", "externalSdCard"));

        Observable<File> mnt = sdCardNames.map(new Func1<String, File>() {
            @Override
            public File call(String sdCard) {
                return new File("/mnt/", sdCard);
            }
        });

        Observable<File> storage = sdCardNames.map(new Func1<String, File>() {
            @Override
            public File call(String sdCard) {
                return new File("/storage/", sdCard);
            }
        });

        final SimpleWrapper<String> sdCard = new SimpleWrapper<>();
        final Subscription subscription = Observable.merge(mnt, storage).filter(new Func1<File, Boolean>() {
            @Override
            public Boolean call(File file) {
                return fullPath.toLowerCase().startsWith(file.getAbsolutePath().toLowerCase());
            }
        }).firstOrDefault(null).subscribe(new Action1<File>() {
            @Override
            public void call(File file) {
                if (file != null) {
                    sdCard.value = file.getAbsolutePath();
                }
            }
        });

        subscription.unsubscribe();
        return sdCard.value;
    }


}
