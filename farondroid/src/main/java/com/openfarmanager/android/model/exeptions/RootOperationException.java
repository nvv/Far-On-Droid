package com.openfarmanager.android.model.exeptions;

/**
 * @author Vlad Namashko
 */
public class RootOperationException extends RuntimeException {

    public RootOperationException() {
    }

    public RootOperationException(String detailMessage) {
        super(detailMessage);
    }
}
