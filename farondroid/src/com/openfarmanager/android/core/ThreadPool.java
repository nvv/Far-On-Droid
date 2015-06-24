package com.openfarmanager.android.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    protected static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    protected static final int KEEP_ALIVE = 1;
    protected static final int MAXIMUM_QUEUE_SIZE = 128;

    protected ThreadPoolExecutor mExecutor;
    protected final Handler mHandler = new Handler(Looper.getMainLooper());

    protected String TAG = "ThreadPool";

    public ThreadPool() {
        this(CPU_COUNT * 2 + 1, CPU_COUNT * 4 + 1);
    }

    public ThreadPool(final int corePoolSize, final int maximumPoolSize) {
        mExecutor = getDefaultExecutor(corePoolSize, maximumPoolSize);
    }

    public ThreadPool(ThreadPoolExecutor executor) {
        mExecutor = executor;
    }

    protected LinkedBlockingQueue<Runnable> getDefaultQueue() {
        return new LinkedBlockingQueue<>(MAXIMUM_QUEUE_SIZE);
    }

    protected ThreadFactory getDefaultThreadFactory() {
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
                return thread;
            }
        };
    }

    private ThreadPoolExecutor getDefaultExecutor(final int corePoolSize, final int maximumPoolSize) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, KEEP_ALIVE, TimeUnit.SECONDS,
                getDefaultQueue(), getDefaultThreadFactory());
    }

    public void shutdown() {
        mExecutor.shutdown();
    }

    public ThreadPoolExecutor getExecutor() {
        return mExecutor;
    }

}
