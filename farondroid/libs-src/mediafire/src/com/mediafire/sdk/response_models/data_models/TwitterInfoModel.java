package com.mediafire.sdk.response_models.data_models;

/**
* Created by Chris on 5/14/2015.
*/
public class TwitterInfoModel {
    private String twitter_id;
    private String date_created;
    private String name;
    private String i18n;
    private String linked;

    public String getTwitterId() {
        if (twitter_id == null) {
            twitter_id = "";
        }
        return twitter_id;
    }

    public String getDateCreated() {
        if (date_created == null) {
            date_created = "";
        }
        return date_created;
    }

    public String getName() {
        if (name == null) {
            name = "";
        }
        return name;
    }

    public String geti18n() {
        if (i18n == null) {
            i18n = "";
        }
        return i18n;
    }

    public boolean isLinked() {
        return "yes".equals(linked);
    }
}
