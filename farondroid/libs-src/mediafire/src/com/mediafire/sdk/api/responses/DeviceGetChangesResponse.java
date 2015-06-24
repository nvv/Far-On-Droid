package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.ChangedItems;

public class DeviceGetChangesResponse extends ApiResponse {
    private ChangedItems updated;
    private ChangedItems deleted;

    private long device_revision;
    private long changes_list_block;


    public long getDeviceRevision() {
        return device_revision;
    }

    public long getChangesListBlock() {
        return changes_list_block;
    }

    public ChangedItems getUpdatedItems() {
        return updated;
    }

    public ChangedItems getDeletedItems() {
        return deleted;
    }
}
