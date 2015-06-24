package com.mediafire.sdk;

/**
 * thrown in extreme cases such as NoSuchAlgorithmException which needs to be addressed by fixing the cause.
 */
public class MFRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 6113319998777232529L;

    public MFRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
