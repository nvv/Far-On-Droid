package com.mediafire.sdk.api.responses.data_models;

/**
* Created by Chris on 2/25/2015.
*/
public class MediaFireNotification {
    private String actor;
    private String timestamp;
    private String resource;
    private String message;

    public String getActor() {
        return actor;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getResource() {
        return resource;
    }

    public String getMessage() {
        return message;
    }
}
