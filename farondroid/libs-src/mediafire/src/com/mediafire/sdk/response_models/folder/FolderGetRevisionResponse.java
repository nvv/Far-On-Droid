package com.mediafire.sdk.response_models.folder;

import com.mediafire.sdk.response_models.ApiResponse;

public class FolderGetRevisionResponse extends ApiResponse {
    private long revision;
    private long epoch;

    public long getRevision() {
        return revision;
    }

    public long getEpoch() {
        return epoch;
    }
}
