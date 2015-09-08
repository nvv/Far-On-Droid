package com.mediafire.sdk.response_models.user;

import com.mediafire.sdk.response_models.ApiResponse;

public class UserRegisterResponse extends ApiResponse {

    String email;
    String created;

    public String getEmail() {
        return email;
    }

    public String getCreated() {
        return created;
    }
}
