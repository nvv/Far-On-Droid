package com.mediafire.sdk.response_models.file;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.FileInfoModel;
import com.mediafire.sdk.response_models.data_models.FileInfosModel;

import java.util.List;

public class FileGetInfoResponse extends ApiResponse {
    private FileInfoModel file_info;
    private List<FileInfosModel> file_infos;

    public FileInfoModel getFileInfo() {
        return this.file_info;
    }

    public List<FileInfosModel> getFileInfos() {
        return file_infos;
    }

}
