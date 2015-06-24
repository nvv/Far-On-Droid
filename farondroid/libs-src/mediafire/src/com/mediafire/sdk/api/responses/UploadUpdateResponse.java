package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.ResumableDoUpload;

public class UploadUpdateResponse extends ApiResponse {
    private ResumableDoUpload doupload;

    public ResumableDoUpload getDoUpload() {
        return doupload;
    }
}
