package com.mediafire.sdk.response_models.upload;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.SimpleDoUploadModel;

public class UploadSimpleResponse extends ApiResponse {
    public SimpleDoUploadModel doupload;

    public SimpleDoUploadModel getDoUpload() {
        return doupload;
    }
}
