package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.MyFilesRevision;

/**
 * Created by Chris on 2/11/2015.
 */
public class FileUpdateResponse extends ApiResponse {
    private String asynchronous;
    private MyFilesRevision myfiles_revision;
    private long device_revision;

    public boolean isAsynchronous() {
        if (this.asynchronous == null) {
            this.asynchronous = "no";
        }
        return !"no".equalsIgnoreCase(this.asynchronous);
    }

    public long getDeviceRevision() {
        return device_revision;
    }

    public MyFilesRevision getMyFilesRevision() {
        return this.myfiles_revision;
    }
}
