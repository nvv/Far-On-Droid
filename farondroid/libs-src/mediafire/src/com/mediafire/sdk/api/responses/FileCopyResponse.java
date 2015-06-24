package com.mediafire.sdk.api.responses;

public class FileCopyResponse extends ApiResponse {

    private int skipped_count;
    private int other_count;
    private int device_revision;
    private String[] new_quickkeys;


    public int getSkippedCount() {
        return skipped_count;
    }

    public int getOtherCount() {
        return other_count;
    }

    public int getDeviceRevision() {
        return device_revision;
    }

    public String[] getNewQuickKeys() {
        return new_quickkeys;
    }
}
