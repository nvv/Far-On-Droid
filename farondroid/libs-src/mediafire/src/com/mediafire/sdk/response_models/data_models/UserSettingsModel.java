package com.mediafire.sdk.response_models.data_models;

public class UserSettingsModel {
    private long max_upload_size;
    private long max_instant_upload_size;
    private boolean validated;
    private boolean instant_uploads_enabled;

    private ShowDownloadPageModel show_download_page;
    private AutoBandwidthModel auto_bandwidth;

    private long used_storage_size;
    private long storage_limit;
    private boolean storage_limit_exceeded;
    private int previous_file_versions;
    private String default_share_link_status;

    public String getDefaultShareLinkStatus() {
        return default_share_link_status;
    }

    public int getPreviousFileVersions() {
        return previous_file_versions;
    }

    public boolean isStorageLimitExceeded() {
        return storage_limit_exceeded;
    }

    public long getStorageLimit() {
        return storage_limit;
    }

    public long getUsedStorageSize() {
        return used_storage_size;
    }

    public AutoBandwidthModel getAutoBandwidth() {
        if(auto_bandwidth == null){
            return new AutoBandwidthModel();
        }
        return auto_bandwidth;
    }

    public ShowDownloadPageModel getShowDownloadPage() {
        if(show_download_page == null){
            return  new ShowDownloadPageModel();
        }
        return show_download_page;
    }

    public boolean isInstantUploadsEnabled() {
        return instant_uploads_enabled;
    }

    public boolean getValidated() {
        return validated;
    }

    public long getMaxInstantUploadSize() {
        return max_instant_upload_size;
    }

    public long getMaxUploadSize() {
        return max_upload_size;
    }

}
