package com.mediafire.sdk.response_models.data_models;

/**
* Created by Chris on 5/14/2015.
*/
public class FileModel {
    private String quickkey;
    private String filename;
    private String description;
    private String size;
    private String privacy;
    private String created;
    private String filetype;
    private String mimetype;
    private String flag;
    private String permissions;
    private String hash;
    private String downloads;
    private String views;
    private String shared_by_user;
    private String password_protected;

    public int getDownloads() {
        if (this.downloads == null) {
            this.downloads = "0";
        }
        return Integer.valueOf(this.downloads);
    }

    public int getViews() {
        if (this.views == null) {
            this.views = "0";
        }
        return Integer.valueOf(this.views);
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

    public String getHash() {
        if (this.hash == null) {
            this.hash = "";
        }
        return this.hash;
    }

    public boolean isPasswordProtected() {
        return "yes".equalsIgnoreCase(this.privacy);
    }

    public boolean isPublic() {
        return "public".equalsIgnoreCase(this.privacy);
    }

    public String getDescription() {
        if (this.description == null) {
            this.description = "";
        }
        return this.description;
    }

    public int getPermissions() {
        if (this.permissions == null) {
            this.permissions = "0";
        }
        return Integer.valueOf(this.permissions);
    }

    public boolean isSharedByUser() {
        return "1".equalsIgnoreCase(this.shared_by_user);
    }

    public String getQuickKey() {
        if (this.quickkey == null) {
            this.quickkey = "";
        }
        return this.quickkey;
    }

    public String getFilename() {
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

    public long getSize() {
        if (this.size == null) {
            this.size = "0";
        }
        return Long.valueOf(this.size);
    }

    public int getFlag() {
        if (this.flag == null) {
            this.flag = "0";
        }
        return Integer.valueOf(this.flag);
    }
}
