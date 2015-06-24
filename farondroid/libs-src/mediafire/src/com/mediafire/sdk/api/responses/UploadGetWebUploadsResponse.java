package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.WebUploads;

/**
 * Created by Chris on 5/19/2015.
 */
public class UploadGetWebUploadsResponse extends ApiResponse {
    private WebUploads[] web_uploads;

    public WebUploads[] getWebUploads() {
        return web_uploads;
    }
}
