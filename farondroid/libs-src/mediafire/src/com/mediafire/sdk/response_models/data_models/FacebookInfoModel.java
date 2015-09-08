package com.mediafire.sdk.response_models.data_models;

/**
* Created by Chris on 5/14/2015.
*/
public class FacebookInfoModel {
    private String facebook_id;
    private String date_created;
    private String facebook_url;
    private String email;
    private String firstname;
    private String lastname;
    private String hometown;
    private String location;
    private String i18n;
    private String timezone;
    private String linked;

    public String getFacebookId() {
        return facebook_id;
    }

    public String getDateCreated() {
        return date_created;
    }

    public String getFacebookUrl() {
        return facebook_url;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstname;
    }

    public String getLastName() {
        return lastname;
    }

    public String getLocation() {
        return location;
    }

    public String getHometown() {
        return hometown;
    }

    public String geti18n() {
        return i18n;
    }

    public String getTimeZone() {
        return timezone;
    }

    public boolean isLinked() {
        if (linked == null) {
            linked = "no";
        }
        return "yes".equals(linked);
    }
}
