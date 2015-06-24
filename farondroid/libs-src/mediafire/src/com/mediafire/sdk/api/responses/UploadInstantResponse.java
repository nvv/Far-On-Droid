package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.Revision;

public class UploadInstantResponse extends ApiResponse {
    private String quickkey;
    private String filename;
    private long device_revision;
    private Revision newrevision;
    private Revision newfolderrevision;

    public String getQuickKey() {
        return quickkey;
    }

    public long getDeviceRevision() {
        return device_revision;
    }

    public Revision getNewRevision() {
        return newrevision;
    }

    public Revision getNewFolderRevision() {
        return newfolderrevision;
    }

    public String getFileName() {
        return filename;
    }
}

