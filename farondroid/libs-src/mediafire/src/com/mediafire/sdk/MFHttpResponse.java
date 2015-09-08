package com.mediafire.sdk;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MFHttpResponse implements MediaFireHttpResponse {
    private final int statusCode;
    private final byte[] body;
    private final Map<String, ? extends List<String>> headers;

    public MFHttpResponse(int statusCode, byte[] body, Map<String, ? extends List<String>> headers) {

        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }
    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public Map<String, ? extends List<String>> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "MFHttpResponse{" +
                "statusCode=" + statusCode +
                ", body=" + (body == null || body.length == 0 ? "null or empty" : body.length > 5000 ? "size: " + body.length : new String(body)) +
                ", headers=" + headers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MFHttpResponse that = (MFHttpResponse) o;

        if (getStatusCode() != that.getStatusCode()) return false;
        if (!Arrays.equals(getBody(), that.getBody())) return false;
        return !(getHeaders() != null ? !getHeaders().equals(that.getHeaders()) : that.getHeaders() != null);

    }

    @Override
    public int hashCode() {
        int result = getStatusCode();
        result = 31 * result + (getBody() != null ? Arrays.hashCode(getBody()) : 0);
        result = 31 * result + (getHeaders() != null ? getHeaders().hashCode() : 0);
        return result;
    }
}
