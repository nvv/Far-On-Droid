package com.mediafire.sdk.config;

import java.util.Map;

public interface MFCredentials {

    /**
     * sets the current credentials (temporarily)
     *
     * @param credentials a Map of credentials
     */
    public void setCredentials(Map<String, String> credentials);

    /**
     * gets the current credentials
     *
     * @return a Map of credentials
     */
    public Map<String, String> getCredentials();

    /**
     * invalidates credentials
     */
    public void invalidate();

    /**
     * sets credentials to valid
     * @return true if operation was successful
     */
    public boolean setValid();

    /**
     * returns if credentials are valid
     *
     * @return true if credentials stored are valid
     */
    public boolean isValid();
}
