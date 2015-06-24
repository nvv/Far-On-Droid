package com.mediafire.sdk.api.responses.data_models;

/**
* Created by Chris on 2/24/2015.
*/
public class Contact {
    private String contact_key;
    private String display_name;
    private String first_name;
    private String last_name;
    private String avatar;
    private String email;
    private String phone;
    private String source_uid;
    private String birthdate;
    private String location;
    private String gender;
    private String website;
    private String options;
    private String created;
    private String contact_type;

    public String getContactKey() {
        return contact_key;
    }

    public String getDisplayName() {
        return display_name;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getSourceUid() {
        return source_uid;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getLocation() {
        return location;
    }

    public String getGender() {
        return gender;
    }

    public String getWebsite() {
        return website;
    }

    public String getOptions() {
        return options;
    }

    public String getCreated() {
        return created;
    }

    public String getContactType() {
        return contact_type;
    }
}
