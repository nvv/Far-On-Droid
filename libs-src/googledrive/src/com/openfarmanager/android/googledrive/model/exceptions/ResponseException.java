package com.openfarmanager.android.googledrive.model.exceptions;

import com.openfarmanager.android.googledrive.App;

/**
 * author: Vlad Namashko
 */
public class ResponseException extends RuntimeException {

    private String mMessage;

    public ResponseException(int messageId) {
        mMessage = App.sInstance.getResources().getString(messageId);
    }

    public String getMessage() {
        return mMessage;
    }

}
