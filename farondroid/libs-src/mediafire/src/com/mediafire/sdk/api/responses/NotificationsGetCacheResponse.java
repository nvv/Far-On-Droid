package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.MediaFireNotification;

/**
 * Created by Chris on 1/19/2015.
 */
public class NotificationsGetCacheResponse extends ApiResponse {
    private MediaFireNotification[] notifications;
    private int num_older;

    public MediaFireNotification[] getNotifications() {
        return notifications;
    }

    public int getNumOlder() {
        return num_older;
    }

}
