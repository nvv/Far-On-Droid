package com.mediafire.sdk.response_models.file;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.MyFilesRevisionModel;

public class FileMoveResponse extends ApiResponse {
    private MyFilesRevisionModel myfiles_revision;

    public MyFilesRevisionModel getMyFilesRevision() {
        return myfiles_revision;
    }
}
