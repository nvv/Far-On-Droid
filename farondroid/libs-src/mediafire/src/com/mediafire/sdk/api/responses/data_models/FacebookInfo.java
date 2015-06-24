package com.mediafire.sdk.api.responses.data_models;

/**
* Created by Chris on 5/14/2015.
*/
public class FacebookInfo {
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
        if (facebook_id == null) {
            facebook_id = "";
        }
        return facebook_id;
    }

    public String getDateCreated() {
        if (date_created == null) {
            date_created = "";
        }
        return date_created;
    }

    public String getFacebookUrl() {
        if (facebook_url == null) {
            facebook_url = "";
        }
        return facebook_url;
    }

    public String getEmail() {
        if (email == null) {
            email = "";
        }
        return email;
    }

    public String getFirstName() {
        if (firstname == null) {
            firstname = "";
        }
        return firstname;
    }

    public String getLastName() {
        if (lastname == null) {
            lastname = "";
        }
        return lastname;
    }

    public String getLocation() {
        if (location == null) {
            location = "";
        }
        return location;
    }

    public String getHometown() {
        if (hometown == null) {
            hometown = "";
        }
        return hometown;
    }

    public String geti18n() {
        if (i18n == null) {
            i18n = "";
        }
        return i18n;
    }

    public String getTimeZone() {
        if (timezone == null) {
            timezone = "";
        }
        return timezone;
    }

    public boolean isLinked() {
        if (linked == null) {
            linked = "no";
        }
        return "yes".equals(linked);
    }
}
