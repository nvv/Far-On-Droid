package com.mediafire.sdk.config;

import com.mediafire.sdk.token.ActionToken;

public class DefaultActionStore implements MFStore<ActionToken> {

    private ActionToken token;
    private final Object lock = new Object();
    private final long threshold;

    public DefaultActionStore(int thresholdMinutes) {
        this.threshold = 1000 * 60 * thresholdMinutes;
    }

    @Override
    public boolean available() {
        synchronized (lock) {
            if (token == null) {
                return false;
            } else if (token.isExpired()) {
                token = null;
                return false;
            } else if (token.isExpiringWithinMillis(threshold)) {
                token = null;
                return false;
            } else if (token.getToken() == null || token.getToken().isEmpty()) {
                token = null;
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public ActionToken get() {
        synchronized (lock) {
            return token;
        }
    }

    @Override
    public void put(ActionToken actionToken) {
        synchronized (lock) {
            this.token = actionToken;
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            this.token = null;
        }
    }

    @Override
    public int getAvailableCount() {
        return available() ? 1 : 0;
    }
}
