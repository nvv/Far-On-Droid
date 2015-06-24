package com.mediafire.sdk.api.responses;

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
