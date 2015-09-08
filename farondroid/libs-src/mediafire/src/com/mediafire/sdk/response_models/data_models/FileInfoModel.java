package com.mediafire.sdk.response_models.data_models;

/**
* Created by Chris on 5/14/2015.
*/
public class FileInfoModel {
    protected String password_protected;
    protected String owner_name;
    protected String shared_by_user;
    protected String parent_folderkey;
    protected String quickkey;
    protected String filename;
    protected String created;
    protected String downloads;
    protected String description;
    protected String size;
    protected String privacy;
    protected String hash;
    protected String filetype;
    protected String mimetype;
    protected String flag;
    protected String permissions;
    protected String revision;

    public String getQuickKey() {
        if (this.quickkey == null) {
            this.quickkey = "";
        }
        return this.quickkey;
    }

    public String getFileName() {
        if (this.filename == null) {
            this.filename = "";
        }
        return this.filename;
    }

    public String getCreated() {
        if (this.created == null) {
            this.created = "";
        }
        return this.created;
    }

    public int getDownloads() {
        if (this.downloads == null) {
            this.downloads = "0";
        }
        return Integer.valueOf(this.downloads);
    }

    public String getDescription() {
        if (this.description == null) {
            this.description = "";
        }
        return this.description;
    }

    public long getSize() {
        if (this.size == null) {
            this.size = "0";
        }
        return Long.valueOf(this.size);
    }

    public boolean isPrivate() {
        if (this.privacy == null) {
            this.privacy = "public";
        }

        return "private".equalsIgnoreCase(this.privacy);
    }

    public boolean isPasswordProtected() {
        if (this.password_protected == null) {
            this.password_protected = "no";
        }

        return "yes".equalsIgnoreCase(this.password_protected);
    }

    public String getHash() {
        if (this.hash == null) {
            this.hash = "";
        }
        return this.hash;
    }

    public String getFileType() {
        if (this.filetype == null) {
            this.filetype = "";
        }
        return this.filetype;
    }

    public String getMimeType() {
        if (this.mimetype == null) {
            this.mimetype = "";
        }
        return this.mimetype;
    }

    public String getOwnerName() {
        if (this.owner_name == null) {
            this.owner_name = "";
        }
        return this.owner_name;
    }

    public int getFlag() {
        if (this.flag == null) {
            this.flag = "-1";
        }
        return Integer.valueOf(this.flag);
    }

    public boolean isSharedByUser() {
        if (this.shared_by_user == null) {
            this.shared_by_user = "yes";
        }

        return !"no".equalsIgnoreCase(this.shared_by_user);
    }

    public int getPermissions() {
        if (this.permissions == null) {
            this.permissions = "-1";
        }

        return Integer.valueOf(this.permissions);
    }

    public String getParentFolderKey() {
        if (parent_folderkey == null || parent_folderkey.isEmpty()) {
            parent_folderkey = "myfiles";
        }
        return parent_folderkey;
    }

    public int getRevision() {
        if (this.revision == null) {
            this.revision = "0";
        }
        return Integer.valueOf(this.revision);
    }
}
