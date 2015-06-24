package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.ResumableUpload;

public class UploadCheckResponse extends ApiResponse {
    private String hash_exists;
    private String in_account;
    private String in_folder;
    private String file_exists;
    private String different_hash;
    private String duplicate_quickkey;
    private long available_space;
    private long used_storage_size;
    private long storage_limit;
    private String storage_limit_exceeded;
    private ResumableUpload resumable_upload;

    public long getUsedStorageSize() {
        return used_storage_size;
    }

    public long getStorageLimit() {
        return storage_limit;
    }

    public String getHashExists() {
        return hash_exists;
    }

    public String getInAccount() {
        return in_account;
    }

    public String getInFolder() {
        return in_folder;
    }

    public String getFileExists() {
        return file_exists;
    }

    public String getDifferentHash() {
        return different_hash;
    }

    public String getStorageLimitExceeded() {
        return storage_limit_exceeded;
    }

    public ResumableUpload getResumableUpload() {
        return resumable_upload;
    }

    public String getDuplicateQuickkey() {
        return duplicate_quickkey;
    }

    public long getAvailableSpace() {
        return available_space;
    }

}
