package com.mediafire.sdk;

public interface MediaFireHttpRequester {
    MediaFireHttpResponse get(MediaFireHttpRequest request) throws MediaFireException;
    MediaFireHttpResponse post(MediaFireHttpRequest request) throws MediaFireException;
    MediaFireHttpsAgent getHttpsAgent();
}
