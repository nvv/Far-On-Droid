package com.mediafire.sdk;

public interface MediaFireSessionToken extends MediaFireToken {
    /**
     * A component used in the construction of session token v2 signatures
     * @return
     */
    String getTime();

    /**
     * A component used in the construction of session token v2 signatures
     * @return
     */
    long getSecretKey();

    /**
     * The token used to determine if the user's password has changed by comparing it with a previously returned pkey.
     * Only returned if email was passed.
     * @return
     */
    String getPkey();

    /**
     * An alternate form of the session user's ID
     * @return
     */
    String getEkey();

    /**
     * Updates the session token secret key
     */
    void update();
}
