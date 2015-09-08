package com.mediafire.sdk;

import java.util.List;
import java.util.Map;

public interface MediaFireHttpResponse {
    /**
     * the status code of the response
     * @return
     */
    int getStatusCode();

    /**
     * the body of the response
     * @return
     */
    byte[] getBody();

    /**
     * response headers of the response
     * @return
     */
    Map<String, ? extends List<String>> getHeaders();
}
