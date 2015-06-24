package com.mediafire.sdk.api.responses.data_models;

/**
* Created by Chris on 5/14/2015.
*/
public class UserInfo {
    private String first_name;
    private String last_name;
    private String display_name;
    private String birth_date;
    private String max_instant_upload_size;
    private String storage_limit;
    private String storage_limit_exceeded;
    private String used_storage_size;
    private String email;
    private String gender;
    private String location;
    private String website;
    private String premium;
    private String options;
    private String ekey;
    private FacebookInfo facebook;
    private TwitterInfo twitter;

    public FacebookInfo getFacebookInfo() {
        if (facebook == null) {
            facebook = new FacebookInfo();
        }
        return facebook;
    }

    public TwitterInfo getTwitterInfo() {
        if (twitter == null) {
            twitter = new TwitterInfo();
        }
        return twitter;
    }

    public String getEKey() {
        if (ekey == null) {
            ekey = "";
        }
        return ekey;
    }

    public String getFirstName() {
        if (first_name == null) {
            first_name = "";
        }
        return first_name;
    }

    public String getLastName() {
        if (last_name == null) {
            last_name = "";
        }
        return last_name;
    }

    public String getDisplayName() {
        if (display_name == null) {
            display_name = "";
        }
        return display_name;
    }

    public String getBirthDate() {
        if (birth_date == null) {
            birth_date = "";
        }
        return birth_date;
    }

    public String getMaxInstantUploadSize() {
        if (max_instant_upload_size == null) {
            max_instant_upload_size = "";
        }
        return max_instant_upload_size;
    }

    public String getStorageLimit() {
        if (storage_limit == null) {
            storage_limit = "";
        }
        return storage_limit;
    }

    public String getUsedStorageSize() {
        if (used_storage_size == null) {
            used_storage_size = "";
        }
        return used_storage_size;
    }

    public String getStorageLimitExceeded() {
        if (storage_limit_exceeded == null) {
            storage_limit_exceeded = "";
        }
        return storage_limit_exceeded;
    }

    public String getEmail() {
        if (email == null) {
            email = "";
        }
        return email;
    }

    public String getGender() {
        if (gender == null) {
            gender = "";
        }
        return gender;
    }

    public String getLocation() {
        if (location == null) {
            location = "";
        }
        return location;
    }

    public String getWebsite() {
        if (website == null) {
            website = "";
        }
        return website;
    }

    public String getPremium() {
        if (premium == null) {
            premium = "";
        }
        return premium;
    }

    public String getOptions() {
        if (options == null) {
            options = "";
        }
        return options;
    }
}
