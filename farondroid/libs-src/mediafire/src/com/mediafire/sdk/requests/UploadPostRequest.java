package com.mediafire.sdk.requests;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Upload request via POST
 */
public class UploadPostRequest extends ApiPostRequest {
    private final Map<String, Object> headerParameters;
    private final byte[] payload;

    public UploadPostRequest(String scheme, String domain, String path, LinkedHashMap<String, Object> query, Map<String, Object> headerParameters, byte[] payload) {
        super(scheme, domain, path, query);
        this.headerParameters = headerParameters;
        this.payload = payload;
    }

    public UploadPostRequest(String path, LinkedHashMap<String, Object> query, Map<String, Object> headerParameters, byte[] payload) {
        this("https", "www.mediafire.com", path, query, headerParameters, payload);
    }

    public byte[] getPayload() {
        return payload;
    }

    public Map<String, Object> getHeaders() {
        return headerParameters;
    }
}
