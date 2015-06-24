package com.mediafire.sdk;

/**
 * Created by Chris on 5/26/2015.
 */
public class MFSessionNotStartedException extends Exception {
    private static final long serialVersionUID = -2343638972399745816L;

    public MFSessionNotStartedException() {
        super("an operation was attempted which requires a session, but a session has not been started");
    }
}
