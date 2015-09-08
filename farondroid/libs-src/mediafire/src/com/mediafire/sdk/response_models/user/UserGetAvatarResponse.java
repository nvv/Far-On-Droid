package com.mediafire.sdk.response_models.user;

import com.mediafire.sdk.response_models.ApiResponse;

public class UserGetAvatarResponse extends ApiResponse {
    private String avatar;

    public String getAvatarUrl() {
        return avatar;
    }
}
