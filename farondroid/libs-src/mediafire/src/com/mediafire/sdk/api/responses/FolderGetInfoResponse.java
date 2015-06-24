package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.FolderInfo;

public class FolderGetInfoResponse extends ApiResponse {
    private FolderInfo folder_info;
    private FolderInfo[] folder_infos;

    public FolderInfo getFolderInfo() {
        return folder_info;
    }

    public FolderInfo[] getFolderInfos() {
        return folder_infos;
    }

}
