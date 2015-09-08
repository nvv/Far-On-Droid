package com.mediafire.sdk.response_models.folder;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.FolderContentsModel;

public class FolderGetContentsResponse extends ApiResponse {
    public FolderContentsModel folder_content;

    public FolderContentsModel getFolderContents() {
        return folder_content;
    }

}
