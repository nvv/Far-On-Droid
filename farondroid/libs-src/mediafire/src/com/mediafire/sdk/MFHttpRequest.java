package com.mediafire.sdk;

import java.util.Arrays;
import java.util.Map;

public class MFHttpRequest implements MediaFireHttpRequest {

    private final String url;
    private final byte[] payload;
    private final Map<String, Object> headers;

    public MFHttpRequest(String url, byte[] payload, Map<String, Object> headers) {

        this.url = url;
        this.payload = payload;
        this.headers = headers;
    }

    @Override
    public String getRequestUrl() {
        return url;
    }

    @Override
    public byte[] getRequestPayload() {
        return payload;
    }

    @Override
    public Map<String, Object> getRequestHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "MFHttpRequest{" +
                "url='" + url + '\'' +
                ", payload=" + (payload == null || payload.length == 0 ? "null or empty" : payload.length > 5000 ? "size: " + payload.length : new String(payload)) +
                ", headers=" + headers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MFHttpRequest that = (MFHttpRequest) o;

        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (!Arrays.equals(payload, that.payload)) return false;
        return !(headers != null ? !headers.equals(that.headers) : that.headers != null);

    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (payload != null ? Arrays.hashCode(payload) : 0);
        result = 31 * result + (headers != null ? headers.hashCode() : 0);
        return result;
    }
}
