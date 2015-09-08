package com.mediafire.sdk.response_models.upload;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.DoUploadResumableModel;
import com.mediafire.sdk.response_models.data_models.ResumableUploadModel;

public class UploadResumableResponse extends ApiResponse {
    private DoUploadResumableModel doupload;
    private ResumableUploadModel resumable_upload;

    public DoUploadResumableModel getDoUpload() {
        return doupload;
    }

    public ResumableUploadModel getResumableUpload() {
        return resumable_upload;
    }

}

