package com.mediafire.sdk.response_models.user;

import com.mediafire.sdk.response_models.ApiResponse;

public class UserGetSessionTokenResponse extends ApiResponse {
    private String session_token;
    private long secret_key;
    private String pkey;
    private String ekey;
    private String time;

    private String permanent_token;

    public String getSessionToken() {
        return session_token;
    }

    public long getSecretKey() {
        return secret_key;
    }

    public String getPkey() {
        return pkey;
    }

    public String getEkey() {
        return ekey;
    }

    public String getTime() {
        return time;
    }

    public String getPermanentToken() {
        return permanent_token;
    }
}
