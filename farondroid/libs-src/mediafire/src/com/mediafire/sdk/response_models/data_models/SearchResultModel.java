package com.mediafire.sdk.response_models.data_models;

/**
* Created by Chris on 5/14/2015.
*/
public class SearchResultModel {
    private String type;
    private String quickkey;
    private String filename;
    private String pass;
    private String created;
    private String size;
    private String mimetype;
    private String filetype;
    private String privacy;
    private String flag;
    private String relevancy;
    private String hash;

    private String folderkey;
    private String name;

    private String parent_folderkey;
    private String parent_name;
    private String password_protected;
    private String byte_count;
    private String total_folders;
    private String total_files;
    private String total_size;
    private String delete_date;


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

    public String getByteCount() {
        if (byte_count == null) {
            byte_count = "";
        }
        return byte_count;
    }

    public int getTotalFolders() {
        if (total_folders == null) {
            total_folders = "0";
        }
        return Integer.valueOf(total_folders);
    }

    public int getTotalFiles() {
        if (total_files == null) {
            total_files = "0";
        }
        return Integer.valueOf(total_files);
    }

    public long getTotalSize() {
        if (total_size == null) {
            total_size = "0";
        }
        return Long.valueOf(total_size);
    }

    public String getDeleteDate() {
        if (delete_date == null) {
            delete_date = "";
        }
        return delete_date;
    }

    public String getType() {
        if (type == null) {
            type = "";
        }
        return type;
    }

    public String getQuickKey() {
        if (quickkey == null) {
            quickkey = "";
        }
        return quickkey;
    }

    public String getFileName() {
        if (filename == null) {
            filename = "";
        }
        return filename;
    }

    public String getParentFolderKey() {
        if (parent_folderkey == null || parent_folderkey.isEmpty()) {
            parent_folderkey = "myfiles";
        }
        return parent_folderkey;
    }

    public String getParentName() {
        if (parent_name == null) {
            parent_name = "";
        }
        return parent_name;
    }

    public String getPass() {
        if (pass == null) {
            pass = "";
        }
        return pass;
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
        return Long.valueOf(size);
    }

    public String getMimeType() {
        if (mimetype == null) {
            mimetype = "";
        }
        return mimetype;
    }

    public String getFileType() {
        if (filetype == null) {
            filetype = "";
        }
        return filetype;
    }

    public boolean isPublic() {
        if (privacy == null) {
            privacy = "private";
        }
        return "public".equalsIgnoreCase(privacy);
    }

    public String getPasswordProtected() {
        if (password_protected == null) {
            password_protected = "";
        }
        return password_protected;
    }

    public int getFlag() {
        if (flag == null) {
            flag = "0";
        }
        return Integer.valueOf(flag);
    }

    public int getRelevancy() {
        if (relevancy == null) {
            relevancy = "0";
        }
        return Integer.valueOf(relevancy);
    }

    public String getHash() {
        if (hash == null) {
            hash = "";
        }
        return hash;
    }
}
