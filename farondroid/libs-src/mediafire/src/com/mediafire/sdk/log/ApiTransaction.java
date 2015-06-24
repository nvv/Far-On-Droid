package com.mediafire.sdk.log;

import com.mediafire.sdk.requests.GetRequest;
import com.mediafire.sdk.requests.HttpApiResponse;
import com.mediafire.sdk.requests.PostRequest;

import java.util.List;
import java.util.Map;

/**
 * Created by Chris on 5/26/2015.
 */
public class ApiTransaction extends MFLog {

    private final String requestUrl;
    private final Map<String, Object> requestHeaders;
    private final String requestPayload;

    private final int responseStatus;
    private final Map<String, List<String>> responseHeaders;
    private final String responseString;
    private final Exception exception;

    private ApiTransaction(PostRequest postRequest, HttpApiResponse response, Exception e) {
        super(System.currentTimeMillis());
        this.requestUrl = postRequest.getUrl();
        this.requestHeaders = postRequest.getHeaders();
        if (postRequest.getPayload() == null) {
            this.requestPayload = null;
        } else if (postRequest.getPayload().length > 2000) {
            this.requestPayload = "payload size: " + String.valueOf(postRequest.getPayload().length);
        } else {
            this.requestPayload = new String(postRequest.getPayload());
        }

        this.responseStatus = response.getStatus();
        this.responseHeaders = response.getHeaderFields();
        if (response.getBytes() != null) {
            this.responseString = new String(response.getBytes());
        } else {
            this.responseString = null;
        }

        this.exception = e;
    }

    public ApiTransaction(PostRequest postRequest, HttpApiResponse response) {
        this(postRequest, response, null);
    }

    public ApiTransaction(PostRequest postRequest, Exception e) {
        this(postRequest, null, e);
    }

    private ApiTransaction(GetRequest getRequest, HttpApiResponse response, Exception e) {
        super(System.currentTimeMillis());
        this.requestUrl = getRequest.getUrl();
        this.requestHeaders = getRequest.getHeaders();
        this.requestPayload = null;

        this.responseStatus = response.getStatus();
        this.responseHeaders = response.getHeaderFields();
        if (response.getBytes() != null) {
            this.responseString = new String(response.getBytes());
        } else {
            this.responseString = null;
        }

        this.exception = e;
    }

    public ApiTransaction(GetRequest getRequest, HttpApiResponse response) {
        this(getRequest, response, null);
    }

    public ApiTransaction(GetRequest getRequest, Exception e) {
        this(getRequest, null, e);
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public Map<String, Object> getRequestHeaders() {
        return requestHeaders;
    }

    public String getRequestPayload() {
        return requestPayload;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseString() {
        return responseString;
    }

    public Exception getException() {
        return exception;
    }
}
