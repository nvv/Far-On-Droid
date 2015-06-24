package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.PollDoUpload;

public class UploadPollUploadResponse extends ApiResponse {
    private PollDoUpload doupload;

    public PollDoUpload getDoUpload() {
        return doupload;
    }

}
