package com.mediafire.sdk.response_models.folder;

import com.mediafire.sdk.response_models.ApiResponse;

public class FolderCreateResponse extends ApiResponse {
    private String folder_key;
    private String upload_key;
    private long device_revision;
    private String folderkey;
    private String name;
    private String description;
    private String tags;
    private String created;
    private String privacy;
    private int file_count;
    private int folder_count;
    private long revision;
    private String dropbox_enabled;
    private String flag;

    public String getFolderKey() {
        if (this.folder_key == null || folder_key.isEmpty()) {
            this.folder_key = "myfiles";
        }
        return this.folder_key;
    }

    public String getUploadKey() {
        return this.upload_key;
    }

    public long getDeviceRevision() {
        return this.device_revision;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTags() {
        return tags;
    }

    public String getCreated() {
        return created;
    }

    public boolean isPrivate() {
        return privacy != null && "private".equals(privacy);

    }

    public int getFileCount() {
        return file_count;
    }

    public int getFolderCount() {
        return folder_count;
    }

    public long getRevision() {
        return revision;
    }

    public boolean isDropboxEnabled() {
        return dropbox_enabled == null || "yes".equalsIgnoreCase(dropbox_enabled);
    }

    public String getFlag() {
        return flag;
    }

}
