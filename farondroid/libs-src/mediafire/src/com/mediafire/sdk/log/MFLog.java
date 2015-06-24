package com.mediafire.sdk.log;

/**
 * Created by Chris on 5/26/2015.
 */
public abstract class MFLog {
    private final long time;

    public MFLog(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
