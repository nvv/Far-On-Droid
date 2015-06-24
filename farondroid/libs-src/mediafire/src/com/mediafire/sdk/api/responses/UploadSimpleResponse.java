package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.SimpleDoUpload;

/**
 * Created by Chris on 5/18/2015.
 */
public class UploadSimpleResponse extends ApiResponse {
    public SimpleDoUpload doupload;

    public SimpleDoUpload getDoUpload() {
        return doupload;
    }
}
