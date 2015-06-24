package com.mediafire.sdk.api.responses;

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
