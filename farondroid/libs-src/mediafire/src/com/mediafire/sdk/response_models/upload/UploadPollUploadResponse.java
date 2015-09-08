package com.mediafire.sdk.response_models.upload;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.DoUploadPollModel;

public class UploadPollUploadResponse extends ApiResponse {
    private DoUploadPollModel doupload;

    public DoUploadPollModel getDoUpload() {
        return doupload;
    }

}
