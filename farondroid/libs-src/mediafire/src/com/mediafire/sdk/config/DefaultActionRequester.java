package com.mediafire.sdk.config;

import com.mediafire.sdk.MFApiException;
import com.mediafire.sdk.MFException;
import com.mediafire.sdk.MFSessionNotStartedException;
import com.mediafire.sdk.api.responses.ApiResponse;
import com.mediafire.sdk.api.responses.UserGetActionTokenResponse;
import com.mediafire.sdk.requests.*;
import com.mediafire.sdk.token.ActionToken;
import com.mediafire.sdk.util.ResponseUtil;

import java.util.LinkedHashMap;

public class DefaultActionRequester implements MFActionRequester {
    private static final int REQUESTED_IMAGE_TOKEN_LIFESPAN_MINUTES = 10;
    private final MFHttpRequester http;
    private final MFSessionRequester sessionRequester;
    private final MFStore<ActionToken> imageStore;
    private final MFStore<ActionToken> uploadStore;

    private boolean sessionStarted;

    public DefaultActionRequester(MFHttpRequester http, MFSessionRequester sessionRequester,
                                  MFStore<ActionToken> imageStore, MFStore<ActionToken> uploadStore) {
        this.http = http;
        this.sessionRequester = sessionRequester;
        this.imageStore = imageStore;
        this.uploadStore = uploadStore;
    }

    @Override
    public void endSession() {
        sessionStarted = false;
        imageStore.clear();
        uploadStore.clear();
    }

    @Override
    public void sessionStarted() {
        sessionStarted = true;
    }

    @Override
    public HttpApiResponse doConversionRequest(ImageRequest imageRequest) throws MFException, MFApiException, MFSessionNotStartedException {
        if (!sessionStarted) {
            throw new MFSessionNotStartedException();
        }

        ActionToken imageToken;
        //borrow token if available
        synchronized (imageStore) {
            if (!imageStore.available()) {
                getNewImageTokenFromSessionRequester();
            }
            imageToken = imageStore.get();

            if (imageToken == null) {
                throw new MFException("could not get image token from store");
            }
        }

        GetRequest getRequest = new GetRequest(imageRequest, imageToken);
        HttpApiResponse httpResponse = http.doApiRequest(getRequest);
        ResponseUtil.validateConversionHttpResponse(httpResponse);

        // return token
        synchronized (imageStore) {
            imageStore.put(imageToken);
        }
        return httpResponse;
    }

    @Override
    public HttpApiResponse doConversionRequest(DocumentRequest documentRequest) throws MFException, MFApiException, MFSessionNotStartedException {
        if (!sessionStarted) {
            throw new MFSessionNotStartedException();
        }

        ActionToken imageToken;
        //borrow token if available
        synchronized (imageStore) {
            if (!imageStore.available()) {
                getNewImageTokenFromSessionRequester();
            }
            imageToken = imageStore.get();

            if (imageToken == null) {
                throw new MFException("could not get image action token from store");
            }
        }

        GetRequest getRequest = new GetRequest(documentRequest, imageToken);
        HttpApiResponse httpResponse = http.doApiRequest(getRequest);
        ResponseUtil.validateConversionHttpResponse(httpResponse);

        // return token
        synchronized (imageStore) {
            imageStore.put(imageToken);
        }
        return httpResponse;
    }

    @Override
    public <T extends ApiResponse> T doUploadRequest(UploadPostRequest uploadRequest, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        if (!sessionStarted) {
            throw new MFSessionNotStartedException();
        }

        ActionToken uploadToken;
        //borrow token if available
        synchronized (uploadStore) {
            if (!uploadStore.available()) {
                getNewUploadTokenFromSessionRequester();
            }
            uploadToken = uploadStore.get();

            if (uploadToken == null) {
                throw new MFException("could not get upload token from store");
            }
        }

        uploadRequest.addSessionToken(uploadToken.getToken());
        PostRequest postRequest = new PostRequest(uploadRequest, uploadRequest.getPayload());
        HttpApiResponse httpResponse = http.doApiRequest(postRequest);
        ResponseUtil.validateHttpResponse(httpResponse);

        // return token
        synchronized (uploadStore) {
            uploadStore.put(uploadToken);
        }
        return ResponseUtil.makeApiResponseFromHttpResponse(httpResponse, classOfT);
    }

    @Override
    public ActionToken borrowImageToken() throws MFException, MFApiException, MFSessionNotStartedException {
        if (!sessionStarted) {
            throw new MFSessionNotStartedException();
        }

        ActionToken imageToken;
        //borrow token if available
        synchronized (imageStore) {
            if (!imageStore.available()) {
                getNewImageTokenFromSessionRequester();
            }
            imageToken = imageStore.get();

            if (imageToken == null) {
                throw new MFException("could not get image action token from store");
            }
        }

        return imageToken;
    }

    private void getNewUploadTokenFromSessionRequester() throws MFException, MFApiException {
        LinkedHashMap<String, Object> query = new LinkedHashMap<String, Object>();
        query.put("type", "upload");
        query.put("lifespan", REQUESTED_IMAGE_TOKEN_LIFESPAN_MINUTES);
        query.put("response_format", "json");
        ApiPostRequest apiPostRequest = new ApiPostRequest("https", "www.mediafire.com", "/api/1.4/user/get_action_token.php", query);
        makeNewUploadTokenRequest(apiPostRequest);
    }

    private void makeNewUploadTokenRequest(ApiPostRequest apiPostRequest) throws MFApiException, MFException {
        UserGetActionTokenResponse apiResponse = sessionRequester.doApiRequest(apiPostRequest, UserGetActionTokenResponse.class);
        // handle the api response by notifying callback and (if successful) set session to started
        handleUploadTokenResponse(apiResponse);
    }

    private void handleUploadTokenResponse(UserGetActionTokenResponse apiResponse) throws MFApiException {
        // throw ApiException if request has api error
        if (apiResponse.hasError()) {
            throw new MFApiException(apiResponse.getError(), apiResponse.getMessage());
        }
        // store token
        ActionToken sessionToken = ActionToken.makeActionTokenFromApiResponse(apiResponse, 1000 * 60 * REQUESTED_IMAGE_TOKEN_LIFESPAN_MINUTES + System.currentTimeMillis());
        uploadStore.put(sessionToken);
    }

    private void getNewImageTokenFromSessionRequester() throws MFException, MFApiException {
        LinkedHashMap<String, Object> query = new LinkedHashMap<String, Object>();
        query.put("type", "image");
        query.put("lifespan", REQUESTED_IMAGE_TOKEN_LIFESPAN_MINUTES);
        query.put("response_format", "json");
        ApiPostRequest apiPostRequest = new ApiPostRequest("https", "www.mediafire.com", "/api/1.4/user/get_action_token.php", query);
        makeNewImageTokenRequest(apiPostRequest);
    }

    private void makeNewImageTokenRequest(ApiPostRequest apiPostRequest) throws MFException, MFApiException {
        UserGetActionTokenResponse apiResponse = sessionRequester.doApiRequest(apiPostRequest, UserGetActionTokenResponse.class);
        handleImageTokenResponse(apiResponse);
    }

    private void handleImageTokenResponse(UserGetActionTokenResponse apiResponse) throws MFApiException {
        // throw ApiException if request has api error
        if (apiResponse.hasError()) {
            throw new MFApiException(apiResponse.getError(), apiResponse.getMessage());
        }
        // store token
        ActionToken sessionToken = ActionToken.makeActionTokenFromApiResponse(apiResponse, 1000 * 60 * REQUESTED_IMAGE_TOKEN_LIFESPAN_MINUTES + System.currentTimeMillis());
        imageStore.put(sessionToken);
    }
}
