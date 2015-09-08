package com.mediafire.sdk.response_models.folder;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.FolderInfoModel;

public class FolderGetInfoResponse extends ApiResponse {
    private FolderInfoModel folder_info;
    private FolderInfoModel[] folder_infos;

    public FolderInfoModel getFolderInfo() {
        return folder_info;
    }

    public FolderInfoModel[] getFolderInfos() {
        return folder_infos;
    }

}
