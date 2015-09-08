package com.mediafire.sdk.response_models.contact;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.ContactModel;

public class ContactFetchResponse extends ApiResponse {

    private int count;
    private int revision;
    private int epoch;
    private ContactModel[] contacts;

    public int getCount() {
        return count;
    }

    public int getEpoch() {
        return epoch;
    }

    public int getRevision() {
        return revision;
    }

    public ContactModel[] getContacts() {
        return contacts;
    }

}
