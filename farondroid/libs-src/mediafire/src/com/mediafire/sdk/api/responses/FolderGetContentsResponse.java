package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.FolderContents;

public class FolderGetContentsResponse extends ApiResponse {
    public FolderContents folder_content;

    public FolderContents getFolderContents() {
        return folder_content;
    }

}
