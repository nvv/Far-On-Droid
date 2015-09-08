package com.mediafire.sdk;

import com.mediafire.sdk.response_models.MediaFireApiResponse;

import java.util.Map;

public interface MediaFireClient {

    /**
     * request that will not append a signature or session token to the request
     * @param request
     * @param classOfT
     * @param <T>
     * @return
     * @throws MediaFireException
     */
    <T extends MediaFireApiResponse> T noAuthRequest(MediaFireApiRequest request, Class<T> classOfT) throws MediaFireException;

    /**
     * request to the mediafire conversion server
     * @param hash
     * @param requestParameters
     * @return
     * @throws MediaFireException
     */
    MediaFireHttpResponse conversionServerRequest(String hash, Map<String, Object> requestParameters) throws MediaFireException;

    /**
     * upload request which uses an Action Token
     * @param request
     * @param classOfT
     * @param <T>
     * @return
     * @throws MediaFireException
     */
    <T extends MediaFireApiResponse> T uploadRequest(MediaFireApiRequest request, Class<T> classOfT) throws MediaFireException;

    /**
     * request that will append a session token
     * @param request
     * @param classOfT
     * @param <T>
     * @return
     * @throws MediaFireException
     */
    <T extends MediaFireApiResponse> T sessionRequest(MediaFireApiRequest request, Class<T> classOfT) throws MediaFireException;

    /**
     * request for a new session token
     * @param <T>
     * @return
     * @throws MediaFireException
     */
    <T extends MediaFireApiResponse> T authenticationRequest(Class<T> classOfT) throws MediaFireException;

    /**
     * the http requester
     * @return
     */
    MediaFireHttpRequester getHttpRequester();

    /**
     * the session store
     * @return
     */
    MediaFireSessionStore getSessionStore();

    /**
     * the credentials store
     * @return
     */
    MediaFireCredentialsStore getCredentialStore();

    /**
     * the hasher
     * @return
     */
    MediaFireHasher getHasher();

    /**
     * the response parser
     * @return
     */
    MediaFireApiResponseParser getResponseParser();

    /**
     * the application id of the client
     * @return
     */
    String getApplicationId();

    /**
     * the api key of the client
     * @return
     */
    String getApiKey();

    /**
     * api version that will always be used (unless null or empty)
     * @return
     */
    String getApiVersion();
}
