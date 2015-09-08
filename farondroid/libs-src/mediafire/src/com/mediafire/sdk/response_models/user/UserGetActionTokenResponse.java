package com.mediafire.sdk.response_models.user;

import com.mediafire.sdk.response_models.ApiResponse;

public class UserGetActionTokenResponse extends ApiResponse {
    public String action_token;

    public String getActionToken() {
        return action_token;
    }
}
