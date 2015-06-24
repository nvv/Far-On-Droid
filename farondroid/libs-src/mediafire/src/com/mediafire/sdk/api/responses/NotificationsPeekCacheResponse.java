package com.mediafire.sdk.api.responses;

/**
 * Created by Chris on 1/19/2015.
 */
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
