package com.mediafire.sdk.config;

import com.mediafire.sdk.MFApiException;
import com.mediafire.sdk.MFException;
import com.mediafire.sdk.MFSessionNotStartedException;
import com.mediafire.sdk.api.responses.ApiResponse;
import com.mediafire.sdk.requests.DocumentRequest;
import com.mediafire.sdk.requests.HttpApiResponse;
import com.mediafire.sdk.requests.ImageRequest;
import com.mediafire.sdk.requests.UploadPostRequest;
import com.mediafire.sdk.token.ActionToken;

public interface MFActionRequester {
    /**
     * ends the session
     */
    void endSession();

    /**
     * sets the started state to true
     */
    void sessionStarted();

    /**
     * makes an image conversion request
     *
     * @param imageRequest the ImageRequest to make
     * @return an HttpApiResponse
     * @throws com.mediafire.sdk.MFException if an exception occurred while making the request
     * @throws MFApiException if an api exception occurred
     */
    public HttpApiResponse doConversionRequest(ImageRequest imageRequest) throws MFException, MFApiException, MFSessionNotStartedException;

    /**
     * makes a document conversion request
     *
     * @param documentRequest the DocumentRequest to make
     * @return an HttpApiResponse
     * @throws com.mediafire.sdk.MFException if an exception occurred while making the request
     * @throws MFApiException if an api exception occurred
     */
    public HttpApiResponse doConversionRequest(DocumentRequest documentRequest) throws MFException, MFApiException, MFSessionNotStartedException;

    /**
     * makes an upload request using an action token
     *
     * @param uploadRequest the UploadPostRequest to make
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return the response stored in the {@param classOfT object}
     * @throws com.mediafire.sdk.MFException if an exception occurred while making the request
     * @throws MFApiException if an api exception occurred
     */
    public <T extends ApiResponse> T doUploadRequest(UploadPostRequest uploadRequest, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException;

    /**
     * attempts to use an image token from the action requester
     * @return an ActionToken
     */
    public ActionToken borrowImageToken() throws MFException, MFApiException, MFSessionNotStartedException;
}
