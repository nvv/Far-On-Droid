package com.openfarmanager.android.googledrive;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author: Vlad Namashko
 */
public class ThreadPool {

    private static ExecutorService sService = Executors.newFixedThreadPool(2);

    public static ThreadPool sInstance;

    static {
        sInstance = new ThreadPool();
    }

    private ThreadPool() {}

    public void runAsynk(Runnable runnable) {
        //noinspection unchecked
        sService.submit(runnable);
    }
}
