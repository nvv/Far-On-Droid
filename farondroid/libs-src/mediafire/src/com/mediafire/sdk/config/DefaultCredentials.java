package com.mediafire.sdk.config;

import java.util.HashMap;
import java.util.Map;

public class DefaultCredentials implements MFCredentials {

    private final Map<String, String> credentials = new HashMap<String, String>();
    private boolean valid;

    public DefaultCredentials() {
    }

    @Override
    public void setCredentials(Map<String, String> credentials) {
        this.credentials.clear();
        this.credentials.putAll(credentials);
    }

    @Override
    public Map<String, String> getCredentials() {
        return credentials;
    }

    @Override
    public void invalidate() {
        valid = false;
        credentials.clear();
    }

    @Override
    public boolean setValid() {
        if (credentials.isEmpty()) {
            valid = false;
            return false;
        } else {
            valid = true;
            return true;
        }
    }

    @Override
    public boolean isValid() {
        return valid;
    }
}
