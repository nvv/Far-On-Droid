package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.MyFilesRevision;

/**
 * Created by Chris on 2/11/2015.
 */
public class FileDeleteResponse extends ApiResponse {
    private MyFilesRevision myfiles_revision;
    private long device_revision;

    public long getDeviceRevision() {
        return device_revision;
    }

    public MyFilesRevision getMyFilesRevision() {
        return this.myfiles_revision;
    }

}
