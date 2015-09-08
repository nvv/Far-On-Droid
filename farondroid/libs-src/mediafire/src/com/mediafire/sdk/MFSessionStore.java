package com.mediafire.sdk;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MFSessionStore implements MediaFireSessionStore {

    private static final long EXPIRE_THRESHOLD = 1000 * 60;

    // tokens
    private final BlockingQueue<MediaFireSessionToken> sessionTokens = new LinkedBlockingQueue<>();
    private MediaFireActionToken uploadToken;
    private MediaFireActionToken imageToken;

    // locks
    private final Object uploadTokenLock = new Object();
    private final Object imageTokenLock = new Object();

    public MFSessionStore() {
    }

    @Override
    public MediaFireSessionToken getSessionTokenV2() {
        MediaFireSessionToken token;
        try {
            token = sessionTokens.poll(3, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
            return null;
        }
        return token;
    }

    @Override
    public boolean store(MediaFireSessionToken token) {
        return sessionTokens.offer(token);
    }

    @Override
    public int getSessionTokenV2Count() {
        return sessionTokens.size();
    }

    @Override
    public boolean isSessionTokenV2Available() {
        return !sessionTokens.isEmpty();
    }

    @Override
    public MediaFireActionToken getActionToken(int type) throws MediaFireException {
        MediaFireActionToken token;
        switch (type) {
            case MediaFireActionToken.TYPE_IMAGE:
                synchronized (imageTokenLock) {
                    token = imageToken;
                }
                break;
            case MediaFireActionToken.TYPE_UPLOAD:
                synchronized (uploadTokenLock) {
                    token = uploadToken;
                }
                break;
            default:
                throw new MediaFireException("invalid token type passed: " + type);
        }

        return token;
    }

    @Override
    public boolean store(MediaFireActionToken token) {
        switch (token.getType()) {
            case MediaFireActionToken.TYPE_IMAGE:
                synchronized (imageTokenLock) {
                    this.imageToken = token;
                    return true;
                }
            case MediaFireActionToken.TYPE_UPLOAD:
                synchronized (uploadTokenLock) {
                    this.uploadToken = token;
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean isActionTokenAvailable(int type) {
        boolean available;
        switch (type) {
            case MediaFireActionToken.TYPE_IMAGE:
                available = isImageTokenAvailable();
                break;
            case MediaFireActionToken.TYPE_UPLOAD:
                available = isUploadTokenAvailable();
                break;
            default:
                available = false;
                break;
        }
        return available;
    }

    private synchronized boolean isImageTokenAvailable() {
        return imageToken != null && !isTokenExpired(imageToken);
    }

    private boolean isTokenExpired(MediaFireActionToken token) {

        long requestTime = token.getRequestTime();
        int lifespanMinutes = token.getLifespan();
        long lifespan = lifespanMinutes * 60 * 1000;

        long expireTime = requestTime + lifespan;

        return System.currentTimeMillis() >= expireTime + EXPIRE_THRESHOLD;
    }

    private synchronized boolean isUploadTokenAvailable() {
        return uploadToken != null && !isTokenExpired(uploadToken);
    }

    @Override
    public void clear() {
        sessionTokens.clear();
        uploadToken = null;
        imageToken = null;
    }
}
