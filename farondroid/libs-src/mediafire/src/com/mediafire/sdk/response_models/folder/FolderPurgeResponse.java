package com.mediafire.sdk.response_models.folder;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.MyFilesRevisionModel;

public class FolderPurgeResponse extends ApiResponse {

    private MyFilesRevisionModel myfiles_revision;
    private long device_revision;

    public long getDeviceRevision() {
        return device_revision;
    }

    public MyFilesRevisionModel getMyFilesRevision() {
        return myfiles_revision;
    }
}
