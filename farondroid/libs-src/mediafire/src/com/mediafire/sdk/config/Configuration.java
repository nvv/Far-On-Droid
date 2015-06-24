package com.mediafire.sdk.config;

import com.mediafire.sdk.token.ActionToken;
import com.mediafire.sdk.token.SessionToken;

/**
 * This class contains a set of interface objects which are then used to instantiate the MediaFire object.
 * The simplest implementation can call Configuration.getDefault()
 */
public class Configuration {

    private MFCredentials credentials;
    private MFHttpRequester httpRequester;
    private MFSessionRequester sessionRequester;
    private MFActionRequester actionRequester;
    private String alternateDomain;
    private final String apiKey;
    private final String appId;

    public Configuration(String appId, String apiKey) {
        this.appId = appId;
        this.apiKey = apiKey;
    }

    public Configuration(String appId) {
        this(appId, null);
    }

    /**
     * returns the MFCredentials in this configuration
     *
     * @return MFCredentials
     */
    public MFCredentials getCredentials() {
        return credentials;
    }

    /**
     * returns the MFHttpRequester in this configuration
     * @return MFHttpRequester
     */
    public MFHttpRequester getHttpRequester() {
        return httpRequester;
    }

    /**
     * returns the MFSessionRequester in this configuration
     * @return MFSessionRequester
     */
    public MFSessionRequester getSessionRequester() {
        return sessionRequester;
    }

    /**
     * returns the MFActionRequester in this configuration
     *
     * @return MFActionRequester
     */
    public MFActionRequester getActionRequester() {
        return actionRequester;
    }

    /**
     * returns the alternate domain to be used in this configuration
     *
     * @return String
     */
    public String getAlternateDomain() {
        return alternateDomain;
    }

    /**
     * gets the current api key in this configuration
     *
     * @return String
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * gets the current app id in this configuration
     *
     * @return String
     */
    public String getAppId() {
        return appId;
    }

    /**
     *  sets the MFCredentials for this configuration
     *
     * @param mediaFireCredentials MFCredentials
     */
    public void setCredentials(MFCredentials mediaFireCredentials) {
        this.credentials = mediaFireCredentials;
        this.sessionRequester.setCredentials(this.credentials);
    }

    /**
     * sets the HttpRequester for this configuration
     *
     * @param mediaFireHttpRequester MFHttpRequester
     */
    public void setHttpRequester(MFHttpRequester mediaFireHttpRequester) {
        this.httpRequester = mediaFireHttpRequester;
    }

    /**
     * sets the MFSessionRequester for this configuration
     *
     * @param mediaFireSessionRequester MFSessionRequester
     */
    public void setSessionRequester(MFSessionRequester mediaFireSessionRequester) {
        this.sessionRequester = mediaFireSessionRequester;
    }

    /**
     * sets the MFActionRequester for this configuration
     *
     * @param mediaFireActionRequester MFActionRequester
     */
    public void setActionRequester(MFActionRequester mediaFireActionRequester) {
        this.actionRequester = mediaFireActionRequester;
    }

    /**
     * sets an alternate domain to use other than www.mediafire.com
     *
     * @param alternateDomain String
     */
    public void setAlternateDomain(String alternateDomain) {
        this.alternateDomain = alternateDomain;
    }

    /**
     * returns a Configuration object using default interface implementations
     *
     * @param appId the application id
     * @param apiKey the api key (can be null)
     * @return a Configuration object.
     */
    public static Configuration createConfiguration(String appId, String apiKey) {
        // store for session tokens
        MFStore<SessionToken> sessionStore = new DefaultSessionStore();
        MFStore<ActionToken> imageStore = new DefaultActionStore(1);
        MFStore<ActionToken> uploadStore = new DefaultActionStore(10);

        MFCredentials credentials = new DefaultCredentials();
        MFHttpRequester httpRequester = new DefaultHttpRequester(5000, 45000);
        MFSessionRequester sessionRequester = new DefaultSessionRequester(credentials, appId, apiKey, httpRequester, sessionStore);
        sessionRequester.setCredentials(credentials);
        MFActionRequester actionRequester = new DefaultActionRequester(httpRequester, sessionRequester, imageStore, uploadStore);

        Configuration configuration = new Configuration(appId, apiKey);
        configuration.setHttpRequester(httpRequester);
        configuration.setSessionRequester(sessionRequester);
        configuration.setActionRequester(actionRequester);
        configuration.setCredentials(credentials);
        return configuration;
    }

    /**
     * returns a Configuration object using default interface implementations
     *
     * @param appId the application id
     * @return a Configuration object.
     */
    public static Configuration createConfiguration(String appId) {
        return createConfiguration(appId, null);
    }
}
