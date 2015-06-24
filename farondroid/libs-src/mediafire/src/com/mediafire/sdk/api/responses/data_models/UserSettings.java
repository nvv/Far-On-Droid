package com.mediafire.sdk.api.responses.data_models;

/**
* Created by Chris on 5/14/2015.
*/
public class UserSettings {
    private long max_upload_size;
    private long max_instant_upload_size;
    private boolean validated;
    private boolean instant_uploads_enabled;

    private ShowDownloadPage show_download_page;
    private AutoBandwidth auto_bandwidth;

    private long used_storage_size;
    private long storage_limit;
    private boolean storage_limit_exceeded;
    private int previous_file_versions;
    private String default_share_link_status;

    public String getDefault_share_link_status() {
        return default_share_link_status;
    }

    public int getPrevious_file_versions() {
        return previous_file_versions;
    }

    public boolean getStorage_limit_exceeded() {
        return storage_limit_exceeded;
    }

    public long getStorage_limit() {
        return storage_limit;
    }

    public long getUsed_storage_size() {
        return used_storage_size;
    }

    public AutoBandwidth getAuto_bandwidth() {
        if(auto_bandwidth == null){
            return new AutoBandwidth();
        }
        return auto_bandwidth;
    }

    public ShowDownloadPage getShow_download_page() {
        if(show_download_page == null){
            return  new ShowDownloadPage();
        }
        return show_download_page;
    }

    public boolean getInstant_uploads_enabled() {
        return instant_uploads_enabled;
    }

    public boolean getValidated() {
        return validated;
    }

    public long getMax_instant_upload_size() {
        return max_instant_upload_size;
    }

    public long getMax_upload_size() {
        return max_upload_size;
    }

    public class ShowDownloadPage {
        private boolean me_from_me;
        private boolean me_from_others;
        private boolean others_from_me;

        public boolean getMe_from_me() {
            return me_from_me;
        }

        public boolean getMe_from_others() {
            return me_from_others;
        }

        public boolean getOthers_from_me() {
            return others_from_me;
        }
    }

    public class AutoBandwidth {
        private boolean enabled;

        public boolean getEnabled() {
            return enabled;
        }
    }
}
