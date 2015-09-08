package com.mediafire.sdk;

public interface MediaFireActionToken extends MediaFireToken {

    int TYPE_IMAGE = 1;
    int TYPE_UPLOAD = 2;

    /**
     * The type of action token. image or upload.
     * @return
     */
    int getType();

    /**
     * The time the token was requested (in millis).
     * @return
     */
    long getRequestTime();

    /**
     * The number of minutes the action token is valid for. 1 to 1440.
     * @return
     */
    int getLifespan();
}
