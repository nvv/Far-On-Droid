package com.mediafire.sdk.response_models.contact;

import com.mediafire.sdk.response_models.ApiResponse;

public class ContactSummaryResponse extends ApiResponse {
    private int direct;
    private int indirect;
    private int user;
    private int mediafire;
    private int facebook;
    private int gmail;
    private int twitter;
    private int sms;
    private int tumblr;

    public int getDirectContactCount() {
        return direct;
    }

    public int getIndirectContactCount() {
        return indirect;
    }

    public int getUserContactCount() {
        return user;
    }

    public int getMediafireContactCount() {
        return mediafire;
    }

    public int getFacebookContactCount() {
        return facebook;
    }

    public int getGmailContactCount() {
        return gmail;
    }

    public int getTwitterContactCount() {
        return twitter;
    }

    public int getSmsContactCount() {
        return sms;
    }

    public int getTumblerContactCount() {
        return tumblr;
    }
}
