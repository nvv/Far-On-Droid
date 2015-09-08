package com.mediafire.sdk.response_models;

public class ApiResponse implements MediaFireApiResponse {
    private String action;
    private String message;
    private String result;
    private int error;
    private String current_api_version;
    private String new_key;

    @Override
    public final String getAction() {
        return action;
    }

    @Override
    public final String getMessage() {
        return message;
    }

    @Override
    public final int getError() {
        return error;
    }

    @Override
    public final String getResult() {
        return result;
    }

    @Override
    public final String getCurrentApiVersion() {
        return current_api_version;
    }

    @Override
    public final boolean hasError() {
        return error != 0;
    }

    @Override
    public boolean needNewKey() {
        return new_key != null && "yes".equals(new_key);
    }
}
