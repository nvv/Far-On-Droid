package com.mediafire.sdk;

public class MediaFireException extends Exception {

    private static final long serialVersionUID = -3122830390839807822L;

    public MediaFireException(String message) {
        super(message);
    }

    public MediaFireException(String message, Throwable cause) {
        super(message, cause);
    }
}
