package com.mediafire.sdk.response_models.device;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.ChangedItemsModel;

public class DeviceGetChangesResponse extends ApiResponse {
    private ChangedItemsModel updated;
    private ChangedItemsModel deleted;

    private long device_revision;
    private long changes_list_block;


    public long getDeviceRevision() {
        return device_revision;
    }

    public long getChangesListBlock() {
        return changes_list_block;
    }

    public ChangedItemsModel getUpdatedItems() {
        return updated;
    }

    public ChangedItemsModel getDeletedItems() {
        return deleted;
    }
}
