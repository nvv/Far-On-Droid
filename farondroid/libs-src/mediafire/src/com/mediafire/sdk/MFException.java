package com.mediafire.sdk;

/**
 * Thrown for most problems caused by API requests (wraps original thrown Exception).
 */
public class MFException extends Exception {
    private static final long serialVersionUID = 9042001401553400722L;

    public MFException(String message) {
        super(message);
    }

    public MFException(String message, Throwable cause) {
        super(message, cause);
    }
}
