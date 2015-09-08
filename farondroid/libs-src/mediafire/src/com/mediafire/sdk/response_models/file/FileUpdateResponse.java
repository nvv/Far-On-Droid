package com.mediafire.sdk.response_models.file;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.MyFilesRevisionModel;

/**
 * Created by Chris on 2/11/2015.
 */
public class FileUpdateResponse extends ApiResponse {
    private String asynchronous;
    private MyFilesRevisionModel myfiles_revision;
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

    public MyFilesRevisionModel getMyFilesRevision() {
        return this.myfiles_revision;
    }
}
