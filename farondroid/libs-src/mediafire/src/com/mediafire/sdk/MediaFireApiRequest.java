package com.mediafire.sdk;

import java.util.Map;

public interface MediaFireApiRequest {

    /**
     * gets the path of the request (e.g. user/get_info)
     * @return
     */
    String getPath();

    /**
     * gets the query parameters that shall be used in the request
     * @return
     */
    Map<String, Object> getQueryParameters();

    /**
     * gets additional headers that shall be used in the request
     * @return
     */
    Map<String, Object> getHeaders();

    /**
     * payload for the request. if request type is REQUEST_TYPE_UPLOAD, this is the binary data. Any other request type, this is ignored.
     * @return
     */
    byte[] getPayload();
}
