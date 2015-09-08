package com.mediafire.sdk.response_models.data_models;

/**
* Created by Chris on 5/14/2015.
*/
public class FolderModel {
    private String folderkey;
    private String name;
    private String description;
    private String tags;
    private String privacy;
    private String created;
    private String flag;
    private String permissions;
    private String size;
    private String revision;
    private String shared;
    private String dropbox_enabled;
    private String folder_count;
    private String file_count;
    private String shared_by_user;

    public int getRevision() {
        if (this.revision == null) {
            this.revision = "0";
        }
        return Integer.valueOf(this.revision);
    }

    public boolean isPublic() {
        if (this.privacy == null) {
            this.privacy = "public";
        }
        return "public".equalsIgnoreCase(this.privacy);
    }

    public boolean isSharedFromOther() {
        if (this.shared == null) {
            this.shared = "yes";
        }
        return "yes".equalsIgnoreCase(this.shared);
    }

    public boolean isDropboxEnabled() {
        if (this.dropbox_enabled == null) {
            this.dropbox_enabled = "no";
        }
        return "yes".equalsIgnoreCase(this.dropbox_enabled);
    }

    public String getDescription() {
        if (this.description == null) {
            this.description = "";
        }
        return this.description;
    }

    public String getTags() {
        if (this.tags == null) {
            this.tags = "";
        }
        return this.tags;
    }

    public int getFolderCount() {
        if (this.folder_count == null) {
            this.folder_count = "0";
        }
        return Integer.valueOf(this.folder_count);
    }

    public int getFileCount() {
        if (this.file_count == null) {
            this.file_count = "0";
        }
        return Integer.valueOf(this.file_count);
    }

    public int getPermissions() {
        if (this.permissions == null) {
            this.permissions = "0";
        }
        return Integer.valueOf(this.permissions);
    }

    public boolean isSharedByUser() {
        if (this.shared_by_user == null) {
            this.shared_by_user = "0";
        }
        return !"0".equalsIgnoreCase(this.shared_by_user);
    }

    public String getFolderkey() {
        if (this.folderkey == null) {
            this.folderkey = "";
        }
        return this.folderkey;
    }

    public String getFolderName() {
        if (this.name == null) {
            this.name = "";
        }
        return this.name;
    }

    public String getCreated() {
        if (this.created == null) {
            this.created = "";
        }
        return this.created;
    }

    public long getSize() {
        if (this.size == null) {
            this.size = "0";
        }
        return Integer.valueOf(this.size);
    }

    public int getFlag() {
        if (this.flag == null) {
            this.flag = "0";
        }
        return Integer.valueOf(this.flag);
    }
}
