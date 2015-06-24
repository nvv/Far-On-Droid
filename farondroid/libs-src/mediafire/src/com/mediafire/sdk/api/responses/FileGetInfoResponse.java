package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.FileInfo;
import com.mediafire.sdk.api.responses.data_models.FileInfos;

import java.util.List;

public class FileGetInfoResponse extends ApiResponse {
    private FileInfo file_info;
    private List<FileInfos> file_infos;

    public FileInfo getFileInfo() {
        return this.file_info;
    }

    public List<FileInfos> getFileInfos() {
        return file_infos;
    }

}
