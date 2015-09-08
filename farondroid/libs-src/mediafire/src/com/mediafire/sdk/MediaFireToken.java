package com.mediafire.sdk;

interface MediaFireToken {
    /**
     * The token used to authenticate the user in API calls.
     * @return
     */
    String getSessionToken();
}
