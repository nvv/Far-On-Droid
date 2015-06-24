package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.ResumableDoUpload;
import com.mediafire.sdk.api.responses.data_models.ResumableUpload;

public class UploadResumableResponse extends ApiResponse {
    private ResumableDoUpload doupload;
    private ResumableUpload resumable_upload;

    public ResumableDoUpload getDoUpload() {
        return doupload;
    }

    public ResumableUpload getResumableUpload() {
        return resumable_upload;
    }

}

