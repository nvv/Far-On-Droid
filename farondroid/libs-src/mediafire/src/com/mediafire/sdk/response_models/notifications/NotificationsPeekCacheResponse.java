package com.mediafire.sdk.response_models.notifications;

import com.mediafire.sdk.response_models.ApiResponse;

public class NotificationsPeekCacheResponse extends ApiResponse {
    private int num_total;
    private int num_unread;

    public int getNumTotal() {
        return num_total;
    }

    public int getNumUnread() {
        return num_unread;
    }
}
