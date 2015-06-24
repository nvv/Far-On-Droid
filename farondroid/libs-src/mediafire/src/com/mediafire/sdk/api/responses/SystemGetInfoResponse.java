package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.TermsOfService;

public class SystemGetInfoResponse extends ApiResponse {

    private TermsOfService terms_of_service;

    public TermsOfService getTermsOfService() {
        return terms_of_service;
    }

}
