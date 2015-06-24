package com.mediafire.sdk;

/**
 * thrown when there is an API error
 */
public class MFApiException extends Exception {
    private static final long serialVersionUID = -9078735624341950919L;
    private final int errorCode;

    public MFApiException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
