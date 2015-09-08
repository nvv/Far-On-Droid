package com.mediafire.sdk;

public interface MediaFireSessionStore {
    /**
     * gets a MediaFireSessionToken.
     * @return null if unavailable
     */
    MediaFireSessionToken getSessionTokenV2();

    /**
     * puts a MediaFireSessionToken in the store
     * @param token true if stored
     */
    boolean store(MediaFireSessionToken token);

    /**
     * gets the count of MediaFireSessionToken available
     * @return
     */
    int getSessionTokenV2Count();

    /**
     * gets whether or not any MediaFireSessionToken are available
     * @return
     */
    boolean isSessionTokenV2Available();

    /**
     * gets a MediaFireActionToken.
     * @param type
     * @return null if unavailable
     */
    MediaFireActionToken getActionToken(int type) throws MediaFireException;

    /**
     * puts a MediaFireActionToken in the store
     * @param token true if stored
     */
    boolean store(MediaFireActionToken token);

    /**
     * gets whether or not a MediaFireActionToken of the type is available
     * @param type
     * @return
     */
    boolean isActionTokenAvailable(int type);

    /**
     * clears the tokens in the store
     */
    void clear();
}
