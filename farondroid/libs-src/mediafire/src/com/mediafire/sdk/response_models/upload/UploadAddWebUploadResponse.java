package com.mediafire.sdk.response_models.upload;

import com.mediafire.sdk.response_models.ApiResponse;

public class UploadAddWebUploadResponse extends ApiResponse {
    private String upload_key;

    public String getUploadKey() {
        return upload_key;
    }
}
