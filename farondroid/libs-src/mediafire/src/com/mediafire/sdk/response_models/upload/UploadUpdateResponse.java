package com.mediafire.sdk.response_models.upload;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.DoUploadResumableModel;

public class UploadUpdateResponse extends ApiResponse {
    private DoUploadResumableModel doupload;

    public DoUploadResumableModel getDoUpload() {
        return doupload;
    }
}
