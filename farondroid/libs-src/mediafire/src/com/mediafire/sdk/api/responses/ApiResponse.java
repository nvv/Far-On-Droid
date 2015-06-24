package com.mediafire.sdk.api.responses;

public class ApiResponse {
    private String action;
    private String message;
    private String result;
    private int error;
    private String current_api_version;
    private String new_key;

    public final String getAction() {
        return action;
    }

    public final String getMessage() {
        return message;
    }

    public final int getError() {
        return error;
    }

    public final String getResult() {
        return result;
    }

    public final String getCurrentApiVersion() {
        return current_api_version;
    }

    public final boolean hasError() {
        return error != 0;
    }

    public boolean needNewKey() {
        return new_key != null && "yes".equals(new_key);
    }
}
