package com.mediafire.sdk.api.responses;

public class FolderCopyResponse extends ApiResponse {
    private String asynchronous;

    private String[] new_folderkeys;
    private long device_revision;

    public boolean isAsynchronous() {
        if (this.asynchronous == null) {
            this.asynchronous = "no";
        }

        return !"no".equalsIgnoreCase(this.asynchronous);
    }

    public String[] getNewFolderKeys() {
        return this.new_folderkeys;
    }

    public long getDeviceRevision() {
        return (int) this.device_revision;
    }
}
