package com.mediafire.sdk.api.responses.data_models;

/**
 * Created by Chris on 5/19/2015.
 */
public class WebUploads {
    private String uploadkey;
    private String active;
    private String quickkey;
    private String filename;
    private String created;
    private int status_code;
    private String status;
    private int error_status;
    private String url;
    private String eta;
    private long size;
    private int percentage;

    public String getUploadKey() {
        return uploadkey;
    }

    public String getActive() {
        return active;
    }

    public String getQuickKey() {
        return quickkey;
    }

    public String getFilename() {
        return filename;
    }

    public String getCreated() {
        return created;
    }

    public int getStatusCode() {
        return status_code;
    }

    public String getStatus() {
        return status;
    }

    public int getErrorStatus() {
        return error_status;
    }

    public String getUrl() {
        return url;
    }

    public String getEta() {
        return eta;
    }

    public long getSize() {
        return size;
    }

    public int getPercentage() {
        return percentage;
    }
}
