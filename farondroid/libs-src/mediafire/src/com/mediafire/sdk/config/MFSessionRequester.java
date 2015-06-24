package com.mediafire.sdk.config;

import com.mediafire.sdk.MFApiException;
import com.mediafire.sdk.MFException;
import com.mediafire.sdk.api.responses.ApiResponse;
import com.mediafire.sdk.requests.ApiPostRequest;

import java.util.List;

public interface MFSessionRequester {
    /**
     * starts a session with credentials
     *
     * @param email email address
     * @param password password
     * @param sessionCallback callback
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException if an api exception occurred
     */
    void startSessionWithEmail(String email, String password, List<OnStartSessionCallback> sessionCallback) throws MFApiException, MFException;

    /**
     * starts a session with credentials
     *
     * @param ekey email address
     * @param password password
     * @param sessionCallback callback
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException if an api exception occurred
     */
    void startSessionWithEkey(String ekey, String password, List<OnStartSessionCallback> sessionCallback) throws MFApiException, MFException;

    /**
     * starts a session with credentials
     *
     * @param facebookAccessToken a facebook access token
     * @param sessionCallback callback
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException if an api exception occurred
     */
    void startSessionWithFacebook(String facebookAccessToken, List<OnStartSessionCallback> sessionCallback) throws MFApiException, MFException;

    /**
     * ends the session
     */
    void endSession();

    /**
     * sets the started state to true
     */
    void sessionStarted();

    /**
     * makes an Api POST request using a session token
     *
     * @param apiPostRequest the ApiPostRequest to make
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return the response stored in the {@param classOfT object}
     * @throws com.mediafire.sdk.MFException if an exception occurred while making the request
     * @throws MFApiException if an api exception occurred
     */
    public <T extends ApiResponse> T doApiRequest(ApiPostRequest apiPostRequest, Class<T> classOfT) throws MFException, MFApiException;

    /**
     * whether sessions are available
     * @return true if sessions are available
     */
    public boolean hasSession();

    /**
     * makes an Api POST request without using a session token
     *
     * @param apiPostRequest the ApiPostRequest to make
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return the response stored in the {@param classOfT object}
     * @throws com.mediafire.sdk.MFException if an exception occurred while making the request
     * @throws MFApiException if an api exception occurred
     */
    public <T extends ApiResponse> T doApiRequestWithoutSession(ApiPostRequest apiPostRequest, Class<T> classOfT) throws MFException, MFApiException;

    /**
     * changes the credentials interface used
     * @param credentials MFCredentials
     */
    public void setCredentials(MFCredentials credentials);

    /**
     * interface used for notification of starting session
     */
    interface OnStartSessionCallback {
        /**
         * called when session has started
         */
        public void onSessionStarted();

        /**
         * called if session failed
         * @param code response code
         * @param message message of fail reason
         */
        public void onSessionFailed(int code, String message);
    }
}
