package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.UserInfo;

public class UserGetInfoResponse extends ApiResponse {

    private UserInfo user_info;

    public UserInfo getUserInfo() {
        return user_info;
    }

}
