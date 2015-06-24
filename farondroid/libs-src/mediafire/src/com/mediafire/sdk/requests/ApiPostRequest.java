package com.mediafire.sdk.requests;

import com.mediafire.sdk.util.HashUtil;
import com.mediafire.sdk.util.RequestUtil;

import java.util.*;

/**
 * ApiPostRequest is used to make api requests via POST
 */
public class ApiPostRequest {
    private static final String DEFAULT_SCHEME = "https";
    private static final String DEFAULT_DOMAIN = "www.mediafire.com";

    private final String scheme;
    private final String domain;
    private final String path;
    private final boolean requiresToken;
    private final LinkedHashMap<String, Object> query = new LinkedHashMap<String, Object>();

    public ApiPostRequest(String scheme, String domain, String path, LinkedHashMap<String, Object> query, boolean requiresToken) {
        this.scheme = scheme;
        this.domain = domain;
        this.path = path;
        this.query.putAll(query);
        if (!this.query.containsKey("response_format")) {
            this.query.put("response_format", "json");
        } else if (!"json".equals(this.query.get("response_format"))) {
            this.query.put("response_format", "json");
        }
        this.requiresToken = requiresToken;
    }

    public ApiPostRequest(String scheme, String domain, String path, LinkedHashMap<String, Object> query) {
        this(scheme, domain, path, query, true);
    }

    public ApiPostRequest(String path, LinkedHashMap<String, Object> query, boolean requiresToken) {
        this(DEFAULT_SCHEME, DEFAULT_DOMAIN, path, query, requiresToken);
    }

    public ApiPostRequest(String path, LinkedHashMap<String, Object> query) {
        this(DEFAULT_SCHEME, DEFAULT_DOMAIN, path, query);
    }

    public ApiPostRequest(ApiPostRequest request) {
        this(request.scheme, request.domain, request.path, request.query);
    }

    public ApiPostRequest(ApiPostRequest request, String alternateDomain) {
        this(request.scheme, alternateDomain, request.path, request.query);
    }

    public String getPath() {
        return path;
    }

    public String getDomain() {
        return domain;
    }

    public String getScheme() {
        return scheme;
    }

    public boolean requiresToken() {
        return requiresToken;
    }

    public void addSessionToken(String token) {
        query.put("session_token", token);
    }

    public void addSignature(String signature) {
        query.put("signature", signature);
    }

    public String getQueryString(boolean encodeValues) {
        return RequestUtil.makeQueryStringFromMap(query, encodeValues);
    }

    public static ApiPostRequest newSessionRequestWithEmail(String apiKey, String appId, String email, String password) {
        LinkedHashMap<String, Object> query = getBaseParamsForNewSessionRequest(appId);
        query.put("email", email);
        query.put("password", password);
        return getApiRequestFromUserCredentials(apiKey, appId, query, email + password);
    }

    public static ApiPostRequest newSessionRequestWithEkey(String apiKey, String appId, String ekey, String password) {
        LinkedHashMap<String, Object> query = getBaseParamsForNewSessionRequest(appId);
        query.put("ekey", ekey);
        query.put("password", password);
        return getApiRequestFromUserCredentials(apiKey, appId, query, ekey + password);
    }

    public static ApiPostRequest newSessionRequestWithFacebook(String apiKey, String appId, String facebookAccessToken) {
        LinkedHashMap<String, Object> query = getBaseParamsForNewSessionRequest(appId);
        query.put("fb_access_token", facebookAccessToken);
        return getApiRequestFromUserCredentials(apiKey, appId, query, facebookAccessToken);
    }

    private static LinkedHashMap<String, Object> getBaseParamsForNewSessionRequest(String appId) {
        LinkedHashMap<String, Object> query = new LinkedHashMap<String, Object>();
        query.put("application_id", appId);
        query.put("token_version", 2);
        query.put("response_format", "json");
        return query;
    }

    private static ApiPostRequest getApiRequestFromUserCredentials(String apiKey, String appId, LinkedHashMap<String, Object> query, String userPortion) {
        if (apiKey != null) {
            query.put("signature", HashUtil.sha1(userPortion + appId + apiKey));
        } else {
            query.put("signature", HashUtil.sha1(userPortion + appId));
        }

        return new ApiPostRequest("/api/1.4/user/get_session_token.php", query);
    }
}
