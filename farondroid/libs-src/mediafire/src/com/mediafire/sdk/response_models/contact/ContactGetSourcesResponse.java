package com.mediafire.sdk.response_models.contact;

import com.mediafire.sdk.response_models.ApiResponse;

public class ContactGetSourcesResponse extends ApiResponse {
    private String[] sources;

    public String[] getSources() {
        return sources;
    }
}
