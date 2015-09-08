package com.mediafire.sdk.response_models.upload;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.RevisionModel;

public class UploadInstantResponse extends ApiResponse {
    private String quickkey;
    private String filename;
    private long device_revision;
    private RevisionModel newrevision;
    private RevisionModel newfolderrevision;

    public String getQuickKey() {
        return quickkey;
    }

    public long getDeviceRevision() {
        return device_revision;
    }

    public RevisionModel getNewRevision() {
        return newrevision;
    }

    public RevisionModel getNewFolderRevision() {
        return newfolderrevision;
    }

    public String getFileName() {
        return filename;
    }
}

