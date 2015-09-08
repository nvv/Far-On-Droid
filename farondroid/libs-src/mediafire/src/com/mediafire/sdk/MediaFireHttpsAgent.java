package com.mediafire.sdk;

import javax.net.ssl.HttpsURLConnection;

public interface MediaFireHttpsAgent {
    /**
     * configures an HttpsURLConnection
     * @param connection
     */
    void configureHttpsUrlConnection(HttpsURLConnection connection);
}
