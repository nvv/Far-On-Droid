package com.mediafire.sdk.response_models.data_models;

/**
* Created by Chris on 2/17/2015.
*/
public class DoUploadPollModel extends DoUploadModel {
    private int status;
    private String description;
    private String fileerror;
    private String quickkey;
    private String size;
    private String revision;
    private String created;
    private String filename;
    private String hash;

    public int getStatusCode() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public int getFileErrorCode() {
        return fileerror == null || fileerror.isEmpty() ? 0 : Integer.parseInt(fileerror);
    }

    public String getQuickKey() {
        return quickkey;
    }

    public String getSize() {
        return size;
    }

    public String getRevision() {
        return revision;
    }

    public String getCreated() {
        return created;
    }

    public String getFilename() {
        return filename;
    }

    public String getHash() {
        return hash;
    }
}
