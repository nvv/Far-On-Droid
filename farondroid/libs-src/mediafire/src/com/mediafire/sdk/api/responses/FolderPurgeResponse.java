package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.MyFilesRevision;

public class FolderPurgeResponse extends ApiResponse {

    private MyFilesRevision myfiles_revision;
    private long device_revision;

    public long getDeviceRevision() {
        return device_revision;
    }

    public MyFilesRevision getMyFilesRevision() {
        return myfiles_revision;
    }
}
