package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.MyFilesRevision;

public class FileMoveResponse extends ApiResponse {
    private MyFilesRevision myfiles_revision;

    public MyFilesRevision getMyfilesRevision() {
        return myfiles_revision;
    }
}
