package com.mediafire.sdk;

import com.mediafire.sdk.response_models.MediaFireApiResponse;

public interface MediaFireApiResponseParser {
    /**
     * parses a response as a byte[] to an ApiResponse of the type passed
     * @param response
     * @param classOfT
     * @param <T>
     * @return null if the response could not be parsed
     */
    <T extends MediaFireApiResponse> T parseResponse(MediaFireHttpResponse response, Class<T> classOfT) throws MediaFireException;

    /**
     * response format (json or xml)
     * @return
     */
    String getResponseFormat();
}
