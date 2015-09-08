package com.mediafire.sdk.response_models.data_models;

/**
* Created by Chris on 5/14/2015.
*/
public class FolderInfoModel {
    private String folderkey;
    private String name;
    private String desc;
    private String tags;
    private String created;
    private String revision;
    private String epoch;
    private String size;
    private String flag;
    private String permissions;
    private String avatar;
    private String privacy;

    private String parent_folderkey;
    private String custom_url;
    private String dbx_enabled;
    private String file_count;
    private String folder_count;
    private String shared_by_user;
    private String owner_name;

    public FolderInfoModel() {
    }


    public String getDescription() {
        if (desc == null) {
            desc = "";
        }
        return desc;
    }

    public String getTags() {
        if (tags == null) {
            tags = "";
        }
        return desc;
    }

    public long getEpoch() {
        if (epoch == null) {
            epoch = "0";
        }
        return Long.valueOf(epoch);
    }

    public String getCustomUrl() {
        if (custom_url == null) {
            custom_url = "";
        }
        return custom_url;
    }

    public String getDbxEnabled() {
        if (dbx_enabled == null) {
            dbx_enabled = "";
        }

        return dbx_enabled;
    }

    public int getFileCount() {
        if (file_count == null) {
            file_count = "0";
        }
        return Integer.valueOf(file_count);
    }

    public int getFolderCount() {
        if (folder_count == null) {
            folder_count = "0";
        }
        return Integer.valueOf(folder_count);
    }

    public int getPermissions() {
        if (permissions == null) {
            permissions = "0";
        }
        return Integer.valueOf(permissions);
    }

    public String getAvatar() {
        if (avatar == null) {
            avatar = "";
        }
        return avatar;
    }

    public String getParentFolderKey() {
        if (parent_folderkey == null || parent_folderkey.isEmpty()) {
            parent_folderkey = "myfiles";
        }
        return parent_folderkey;
    }

    public boolean isSharedByUser() {
        if (shared_by_user == null) {
            shared_by_user = "0";
        }
        return !"0".equalsIgnoreCase(shared_by_user);
    }

    public int getRevision() {
        if (revision == null) {
            revision = "0";
        }
        return Integer.valueOf(revision);
    }

    public String getOwnerName() {
        if (owner_name == null) {
            owner_name = "";
        }
        return owner_name;
    }

    public String getFolderKey() {
        if (folderkey == null) {
            folderkey = "";
        }
        return folderkey;
    }

    public String getFolderName() {
        if (name == null) {
            name = "";
        }
        return name;
    }

    public String getCreated() {
        if (created == null) {
            created = "";
        }
        return created;
    }

    public long getSize() {
        if (size == null) {
            size = "0";
        }
        return Integer.valueOf(size);
    }

    public int getFlag() {
        if (flag == null) {
            flag = "0";
        }
        return Integer.valueOf(flag);
    }

    public boolean isPrivate() {
        return "private".equalsIgnoreCase(privacy);
    }

    public int getTotalItems() {
        return getFileCount() + getFolderCount();
    }

}
