package com.mediafire.sdk.response_models.upload;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.WebUploadsModel;

public class UploadGetWebUploadsResponse extends ApiResponse {
    private WebUploadsModel[] web_uploads;

    public WebUploadsModel[] getWebUploads() {
        return web_uploads;
    }
}
