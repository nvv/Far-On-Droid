package com.mediafire.sdk.response_models.contact;

import com.mediafire.sdk.response_models.ApiResponse;

public class ContactAddResponse extends ApiResponse {
    private String contact_keys;

    public String getContactKey() {
        return contact_keys;
    }
}
