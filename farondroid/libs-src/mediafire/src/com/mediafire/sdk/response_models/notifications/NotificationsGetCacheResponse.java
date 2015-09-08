package com.mediafire.sdk.response_models.notifications;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.NotificationModel;

public class NotificationsGetCacheResponse extends ApiResponse {
    private NotificationModel[] notifications;
    private int num_older;

    public NotificationModel[] getNotifications() {
        return notifications;
    }

    public int getNumOlder() {
        return num_older;
    }

}
