package com.mediafire.sdk.response_models.user;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.UserInfoModel;

public class UserGetInfoResponse extends ApiResponse {

    private UserInfoModel user_info;

    public UserInfoModel getUserInfo() {
        return user_info;
    }

}
