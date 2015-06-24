package com.mediafire.sdk.requests;

import com.mediafire.sdk.util.RequestUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * a POST request used by MFHttpRequester
 */
public class PostRequest extends ApiPostRequest {
    private static final String CHARSET = "UTF-8";
    private final String url;
    private final LinkedHashMap<String, Object> headers = new LinkedHashMap<String, Object>();
    private final byte[] payload;


    public PostRequest(ApiPostRequest apiPostRequest) {
        super(apiPostRequest);
        this.url = RequestUtil.makeUrlFromApiRequest(apiPostRequest);
        this.payload = RequestUtil.makeQueryPayloadFromApiRequest(apiPostRequest);
        this.headers.put("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);
        this.headers.put("Content-Length", payload.length);
        this.headers.put("Accept-Charset", "UTF-8");
    }

    public PostRequest(UploadPostRequest uploadRequest, byte[] payload) {
        super(uploadRequest);
        this.url = RequestUtil.makeUrlFromUploadRequest(uploadRequest);
        this.payload = uploadRequest.getPayload();
        this.headers.putAll(uploadRequest.getHeaders());
        this.headers.put("Content-Type", "application/octet-stream");
        this.headers.put("Content-Length", payload.length);
        this.headers.put("Accept-Charset", "UTF-8");
    }

    public String getUrl() {
        return url;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public byte[] getPayload() {
        return payload;
    }
}
