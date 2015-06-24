package com.mediafire.sdk.config;

import com.mediafire.sdk.token.SessionToken;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultSessionStore implements MFStore<SessionToken> {

    private final Queue<SessionToken> sessionTokens = new LinkedBlockingQueue<SessionToken>();

    @Override
    public boolean available() {
        return !sessionTokens.isEmpty();
    }

    @Override
    public SessionToken get() {
        return sessionTokens.poll();
    }

    @Override
    public void put(SessionToken sessionToken) {
        sessionTokens.offer(sessionToken);
    }

    @Override
    public void clear() {
        sessionTokens.clear();
    }

    @Override
    public int getAvailableCount() {
        return sessionTokens.size();
    }
}
