package com.mediafire.sdk;

import java.util.Map;

public interface MediaFireHttpRequest {
    /**
     * url of the request
     * @return
     */
    String getRequestUrl();

    /**
     * payload of the request
     * @return
     */
    byte[] getRequestPayload();

    /**
     * headers of the request
     * @return
     */
    Map<String, Object> getRequestHeaders();
}
