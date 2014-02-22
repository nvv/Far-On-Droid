package com.openfarmanager.android.model.exeptions;

/**
 * @author Vlad Namashko
 */
public class InAppAuthException extends RuntimeException {

    private String mErrorMessage;

    public InAppAuthException(String errorCode) {
        super();
        mErrorMessage = errorCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

}
