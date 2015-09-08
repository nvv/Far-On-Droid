package com.mediafire.sdk.response_models;

public interface MediaFireApiResponse {
    String getAction();

    String getMessage();

    int getError();

    String getResult();

    String getCurrentApiVersion();

    boolean hasError();

    boolean needNewKey();
}
