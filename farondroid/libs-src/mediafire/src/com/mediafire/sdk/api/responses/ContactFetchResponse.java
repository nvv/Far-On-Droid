package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.Contact;

public class ContactFetchResponse extends ApiResponse {

    private int count;
    private int revision;
    private int epoch;
    private Contact[] contacts;

    public int getCount() {
        return count;
    }

    public int getEpoch() {
        return epoch;
    }

    public int getRevision() {
        return revision;
    }

    public Contact[] getContacts() {
        return contacts;
    }

}
