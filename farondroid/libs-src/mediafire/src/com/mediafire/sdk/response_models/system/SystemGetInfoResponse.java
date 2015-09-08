package com.mediafire.sdk.response_models.system;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.TermsOfServiceModel;

public class SystemGetInfoResponse extends ApiResponse {

    private TermsOfServiceModel terms_of_service;

    public TermsOfServiceModel getTermsOfService() {
        return terms_of_service;
    }

}
