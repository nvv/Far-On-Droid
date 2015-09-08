package com.mediafire.sdk;

import com.mediafire.sdk.response_models.MediaFireApiResponse;
import com.mediafire.sdk.response_models.user.UserGetActionTokenResponse;
import com.mediafire.sdk.response_models.user.UserGetSessionTokenResponse;
import com.mediafire.sdk.util.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MFClient implements MediaFireClient {

    private static final String UTF8 = "UTF-8";
    private static final int SESSION_TOKEN_VERSION = 2;

    private final String apiVersion;
    private final MediaFireHttpRequester requester;
    private final MediaFireSessionStore sessionStore;
    private final MediaFireCredentialsStore credentialsStore;
    private final MediaFireHasher hasher;
    private final MediaFireApiResponseParser parser;
    private final String applicationId;
    private final String apiKey;
    private final Object storeLock = new Object();

    private MFClient(Builder builder) {
        this.apiVersion = builder.apiVersion;
        this.requester = builder.requester;
        this.sessionStore = builder.sessionStore;
        this.credentialsStore = builder.credentialsStore;
        this.hasher = builder.hasher;
        this.parser = builder.parser;
        this.applicationId = builder.applicationId;
        this.apiKey = builder.apiKey;
    }

    @Override
    public <T extends MediaFireApiResponse> T noAuthRequest(MediaFireApiRequest request, Class<T> classOfT) throws MediaFireException {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("response_format", getResponseParser().getResponseFormat());
        if (request.getQueryParameters() != null) {
            query.putAll(request.getQueryParameters());
        }

        byte[] payload = makeQueryStringFromMap(query, true).getBytes();
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded;charset=" + UTF8);
        headers.put("Content-Length", payload.length);
        headers.put("Accept-Charset", "UTF-8");

        String baseUrl = "https://www.mediafire.com";

        StringBuilder url = new StringBuilder();
        url.append(baseUrl).append("/api");

        if (!TextUtils.isEmpty(getApiVersion())) {
            url.append("/").append(getApiVersion());
        }
        url.append(request.getPath());

        MediaFireHttpRequest mediaFireHttpRequest = new MFHttpRequest(url.toString(), payload, headers);
        MediaFireHttpResponse mediaFireHttpResponse = requester.post(mediaFireHttpRequest);
        return getResponseParser().parseResponse(mediaFireHttpResponse, classOfT);
    }

    @Override
    public MediaFireHttpResponse conversionServerRequest(String hash, Map<String, Object> requestParameters) throws MediaFireException {
        String baseUrl = "https://www.mediafire.com";

        StringBuilder url = new StringBuilder();
        url.append(baseUrl);
        url.append("/conversion_server.php?");
        if (hash == null || hash.length() < 4) {
            throw new MediaFireException("invalid hash passed in conversion request");
        }

        url.append(hash.substring(0, 4));
        url.append("&");

        MediaFireActionToken mediaFireActionToken;

        synchronized (getSessionStore()) {
            if (!getSessionStore().isActionTokenAvailable(MediaFireActionToken.TYPE_IMAGE)) {
                mediaFireActionToken = requestNewActionToken(MediaFireActionToken.TYPE_IMAGE);
                if (mediaFireActionToken == null) {
                    throw new MediaFireException("could not request action token type " + MediaFireActionToken.TYPE_IMAGE);
                }
                getSessionStore().store(mediaFireActionToken);
            } else {
                mediaFireActionToken = getSessionStore().getActionToken(MediaFireActionToken.TYPE_IMAGE);
            }
        }

        if (mediaFireActionToken == null) {
            throw new MediaFireException("could not get action token type " + MediaFireActionToken.TYPE_IMAGE + " from store");
        }

        requestParameters.put("session_token", mediaFireActionToken.getSessionToken());

        String encodedQuery = makeQueryStringFromMap(requestParameters, true);

        url.append(encodedQuery);

        MediaFireHttpRequest mediaFireHttpRequest = new MFHttpRequest(url.toString(), null, null);
        return getHttpRequester().get(mediaFireHttpRequest);
    }

    @Override
    public <T extends MediaFireApiResponse> T uploadRequest(MediaFireApiRequest request, Class<T> classOfT) throws MediaFireException {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("response_format", getResponseParser().getResponseFormat());
        query.putAll(request.getQueryParameters());

        MediaFireActionToken mediaFireActionToken;

        synchronized (storeLock) {
            if (!getSessionStore().isActionTokenAvailable(MediaFireActionToken.TYPE_UPLOAD)) {
                mediaFireActionToken = requestNewActionToken(MediaFireActionToken.TYPE_UPLOAD);
                if (mediaFireActionToken == null) {
                    throw new MediaFireException("could not request action token type " + MediaFireActionToken.TYPE_IMAGE);
                }
                getSessionStore().store(mediaFireActionToken);
            } else {
                mediaFireActionToken = getSessionStore().getActionToken(MediaFireActionToken.TYPE_UPLOAD);
            }
        }

        if (mediaFireActionToken == null) {
            throw new MediaFireException("could not get action token type " + MediaFireActionToken.TYPE_UPLOAD + " from store");
        }

        query.put("session_token", mediaFireActionToken.getSessionToken());

        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/octet-stream");
        headers.put("Content-Length", request.getPayload().length);
        headers.put("Accept-Charset", "UTF-8");
        if (request.getHeaders() != null) {
            headers.putAll(request.getHeaders());
        }

        String baseUrl = "https://www.mediafire.com";
        StringBuilder url = new StringBuilder();
        url.append(baseUrl).append("/api");
        if (!TextUtils.isEmpty(getApiVersion())) {
            url.append("/").append(getApiVersion());
        }
        url.append(request.getPath()).append("?");

        String encodedQuery = makeQueryStringFromMap(query, true);

        url.append(encodedQuery);

        MediaFireHttpRequest mediaFireHttpRequest = new MFHttpRequest(url.toString(), request.getPayload(), headers);
        MediaFireHttpResponse mediaFireHttpResponse = getHttpRequester().post(mediaFireHttpRequest);
        return getResponseParser().parseResponse(mediaFireHttpResponse, classOfT);
    }

    @Override
    public <T extends MediaFireApiResponse> T sessionRequest(MediaFireApiRequest request, Class<T> classOfT) throws MediaFireException {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("response_format", getResponseParser().getResponseFormat());
        if (request.getQueryParameters() != null) {
            query.putAll(request.getQueryParameters());
        }

        String baseUrl = "https://www.mediafire.com";
        StringBuilder url = new StringBuilder();
        url.append(baseUrl);
        StringBuilder uri = new StringBuilder();
        uri.append("/api");
        if (!TextUtils.isEmpty(getApiVersion())) {
            uri.append("/").append(getApiVersion());
        }
        uri.append(request.getPath());

        MediaFireSessionToken mediaFireSessionToken;

        synchronized (storeLock) {
            if (!getSessionStore().isSessionTokenV2Available()) {
                mediaFireSessionToken = requestNewSessionToken();
            } else {
                mediaFireSessionToken = getSessionStore().getSessionTokenV2();
            }
        }

        if (mediaFireSessionToken == null) {
            throw new MediaFireException("could not get session token from store");
        }

        query.put("session_token", mediaFireSessionToken.getSessionToken());

        String signature = createSignatureForAuthenticatedRequest(mediaFireSessionToken.getSecretKey(), mediaFireSessionToken.getTime(), uri.toString(), query);

        query.put("signature", signature);


        String encodedQuery = makeQueryStringFromMap(query, true);

        Map<String, Object> headers = createHeadersUsingQueryAsPostBody(encodedQuery);

        url.append(uri);

        MediaFireHttpRequest mediaFireHttpRequest = new MFHttpRequest(url.toString(), encodedQuery.getBytes(), headers);
        MediaFireHttpResponse mediaFireHttpResponse = getHttpRequester().post(mediaFireHttpRequest);

        T response = getResponseParser().parseResponse(mediaFireHttpResponse, classOfT);

        if (!response.hasError() && response.needNewKey()) {
            mediaFireSessionToken.update();
            getSessionStore().store(mediaFireSessionToken);
        }

        return response;
    }

    @Override
    public <T extends MediaFireApiResponse> T authenticationRequest(Class<T> classOfT) throws MediaFireException {
        int credentialType = getCredentialStore().getTypeStored();

        Map<String, Object> query = new LinkedHashMap<>();
        query.put("response_format", getResponseParser().getResponseFormat());
        query.put("token_version", SESSION_TOKEN_VERSION);
        query.put("application_id", getApplicationId());

        StringBuilder unhashedSignature = new StringBuilder();
        switch (credentialType) {
            case MediaFireCredentialsStore.TYPE_EMAIL:
                MediaFireCredentialsStore.EmailCredentials emailCredentials = getCredentialStore().getEmailCredentials();
                query.put("email", emailCredentials.getEmail());
                query.put("password", emailCredentials.getPassword());
                unhashedSignature.append(emailCredentials.getEmail()).append(emailCredentials.getPassword());
                break;
            case MediaFireCredentialsStore.TYPE_EKEY:
                MediaFireCredentialsStore.EkeyCredentials ekeyCredentials = getCredentialStore().getEkeyCredentials();
                query.put("ekey", ekeyCredentials.getEkey());
                query.put("password", ekeyCredentials.getPassword());
                unhashedSignature.append(ekeyCredentials.getEkey()).append(ekeyCredentials.getPassword());
                break;
            case MediaFireCredentialsStore.TYPE_FACEBOOK:
                MediaFireCredentialsStore.FacebookCredentials facebookCredentials = getCredentialStore().getFacebookCredentials();
                query.put("fb_access_token", facebookCredentials.getFacebookAccessToken());
                unhashedSignature.append(facebookCredentials.getFacebookAccessToken());
                break;
            case MediaFireCredentialsStore.TYPE_TWITTER:
                MediaFireCredentialsStore.TwitterCredentials twitterCredentials = getCredentialStore().getTwitterCredentials();
                query.put("tw_oauth_token", twitterCredentials.getTwitterOauthToken());
                query.put("tw_oauth_token_secret", twitterCredentials.getTwitterOauthTokenSecret());
                unhashedSignature.append(twitterCredentials.getTwitterOauthToken()).append(twitterCredentials.getTwitterOauthTokenSecret());
                break;
            case MediaFireCredentialsStore.TYPE_NONE:
            default:
                throw new MediaFireException("no credentials stored, cannot authenticate");
        }

        unhashedSignature.append(getApplicationId());

        if (!TextUtils.isEmpty(getApiKey())) {
            unhashedSignature.append(getApiKey());
        }

        String hashedSignature = getHasher().sha1(unhashedSignature.toString());

        query.put("signature", hashedSignature);

        byte[] payload = makeQueryStringFromMap(query, true).getBytes();

        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded;charset=" + UTF8);
        headers.put("Content-Length", payload.length);
        headers.put("Accept-Charset", "UTF-8");

        String baseUrl = "https://www.mediafire.com";
        StringBuilder url = new StringBuilder();
        url.append(baseUrl).append("/api");
        if (!TextUtils.isEmpty(getApiVersion())) {
            url.append("/").append(getApiVersion());
        }

        url.append("/user/get_session_token.php");

        MediaFireHttpRequest mediaFireHttpRequest = new MFHttpRequest(url.toString(), payload, headers);
        MediaFireHttpResponse mediaFireHttpResponse = getHttpRequester().post(mediaFireHttpRequest);
        return getResponseParser().parseResponse(mediaFireHttpResponse, classOfT);
    }

    @Override
    public String getApplicationId() {
        return applicationId;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String getApiVersion() {
        return apiVersion;
    }

    @Override
    public MediaFireHttpRequester getHttpRequester() {
        return requester;
    }

    @Override
    public MediaFireSessionStore getSessionStore() {
        return sessionStore;
    }

    @Override
    public MediaFireCredentialsStore getCredentialStore() {
        return credentialsStore;
    }

    @Override
    public MediaFireHasher getHasher() {
        return hasher;
    }

    @Override
    public MediaFireApiResponseParser getResponseParser() {
        return parser;
    }

    public MediaFireActionToken requestNewActionToken(int type) throws MediaFireException {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("response_format", getResponseParser().getResponseFormat());

        int lifespan;
        switch (type) {
            case MediaFireActionToken.TYPE_IMAGE:
                lifespan = 60;
                query.put("type", "image");
                break;
            case MediaFireActionToken.TYPE_UPLOAD:
                lifespan = 360;
                query.put("type", "upload");
                break;
            default:
                throw new MediaFireException("invalid action token type passed: " + type);
        }

        query.put("lifespan", lifespan);

        MediaFireApiRequest request = new MFApiRequest("/user/get_action_token.php", query, null, null);
        UserGetActionTokenResponse response = sessionRequest(request, UserGetActionTokenResponse.class);
        if (response.hasError()) {
            return null;
        }
        String sessionToken = response.getActionToken();
        return new MFActionToken(sessionToken, type, System.currentTimeMillis(), lifespan);
    }

    public MediaFireSessionToken requestNewSessionToken() throws MediaFireException {

        UserGetSessionTokenResponse response = authenticationRequest(UserGetSessionTokenResponse.class);
        if (response.hasError()) {
            return null;
        }

        String sessionToken = response.getSessionToken();
        String time = response.getTime();
        long secretKey = response.getSecretKey();
        String pkey = response.getPkey();
        String ekey = response.getEkey();
        MFSessionToken token = new MFSessionToken(sessionToken, time, secretKey, pkey, ekey);
        return token;
    }

    private Map<String, Object> createHeadersUsingQueryAsPostBody(String encodedQuery) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("Accept-Charset", "UTF-8");
        headers.put("Content-Length", encodedQuery.length());
        headers.put("Content-Type", "application/x-www-form-urlencoded;charset=" + UTF8);
        return headers;
    }

    private String makeQueryStringFromMap(Map<String, Object> query, boolean encoded) throws MediaFireException {
        StringBuilder sb = new StringBuilder();
        for (String key : query.keySet()) {
            sb.append(constructQueryKVPair(key, query.get(key), encoded));
        }
        return sb.toString().replaceFirst("&", "");
    }

    private String constructQueryKVPair(String key, Object value, boolean encoded) throws MediaFireException {
        if (encoded) {
            try {
                return "&" + key + "=" + URLEncoder.encode(String.valueOf(value), UTF8);
            } catch (UnsupportedEncodingException e) {
                throw new MediaFireException("could not encode string using " + UTF8, e);
            }
        } else {
            return "&" + key + "=" + value;
        }
    }

    private String createSignatureForAuthenticatedRequest(long secretKey, String time, String uri, Map<String, Object> query) throws MediaFireException {
        long secretKeyMod256 = secretKey % 256;
        String queryMap = makeQueryStringFromMap(query, false);
        String hashTarget = secretKeyMod256 + time + uri + "?" + queryMap;
        String signature = getHasher().md5(hashTarget);
        return signature;
    }

    public static class Builder {

        private static final MediaFireHttpRequester DEFAULT_REQUESTER = new MFHttpRequester(45000, 45000);
        private static final MediaFireSessionStore DEFAULT_SESSION_STORE = new MFSessionStore();
        private static final MediaFireCredentialsStore DEFAULT_CREDENTIALS_STORE = new MFCredentialsStore();
        private static final MediaFireHasher DEFAULT_HASHER = new MFHasher();
        private static final MediaFireApiResponseParser DEFAULT_PARSER = new MFApiResponseParser();

        private final String applicationId;
        private final String apiKey;

        private String apiVersion;
        private MediaFireHttpRequester requester = DEFAULT_REQUESTER;
        private MediaFireSessionStore sessionStore = DEFAULT_SESSION_STORE;
        private MediaFireCredentialsStore credentialsStore = DEFAULT_CREDENTIALS_STORE;
        private MediaFireHasher hasher = DEFAULT_HASHER;
        private MediaFireApiResponseParser parser = DEFAULT_PARSER;

        public Builder(String applicationId, String apiKey) {

            this.applicationId = applicationId;
            this.apiKey = apiKey;
        }

        public Builder parser(MediaFireApiResponseParser parser) {
            this.parser = parser;
            return this;
        }

        public Builder hasher(MediaFireHasher hasher) {
            this.hasher = hasher;
            return this;
        }

        public Builder credentialStore(MediaFireCredentialsStore credentialsStore) {
            this.credentialsStore = credentialsStore;
            return this;
        }

        public Builder sessionStore(MediaFireSessionStore sessionStore) {
            this.sessionStore = sessionStore;
            return this;
        }

        public Builder httpRequester(MediaFireHttpRequester requester) {
            this.requester = requester;
            return this;
        }

        public Builder apiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
            return this;
        }

        public MFClient build() {
            return new MFClient(this);
        }
    }
}
