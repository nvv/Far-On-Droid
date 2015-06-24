package com.mediafire.sdk.api.responses;

public class UserSetAvatarResponse extends ApiResponse {
    private String quick_key;
    private String upload_key;

    public String getQuickKey() {
        return quick_key;
    }

    public String getUploadKey() {
        return upload_key;
    }
}
