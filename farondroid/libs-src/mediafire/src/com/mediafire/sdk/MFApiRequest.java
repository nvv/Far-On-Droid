package com.mediafire.sdk;

import java.util.Arrays;
import java.util.Map;

public class MFApiRequest implements MediaFireApiRequest {
    private final String path;
    private final Map<String, Object> queryParameters;
    private final byte[] payload;
    private final Map<String, Object> headers;

    public MFApiRequest(String path, Map<String, Object> queryParameters, byte[] payload, Map<String, Object> headers) {
        this.payload = payload;
        this.path = path;
        this.queryParameters = queryParameters;
        this.headers = headers;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Map<String, Object> getQueryParameters() {
        return queryParameters;
    }

    @Override
    public Map<String, Object> getHeaders() {
        return headers;
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "MFApiRequest{" +
                "path='" + path + '\'' +
                ", queryParameters=" + queryParameters +
                ", payload=" + (payload == null || payload.length == 0 ? "null or empty" : payload.length > 5000 ? "size: " + payload.length : new String(payload)) +
                ", headers=" + headers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MFApiRequest that = (MFApiRequest) o;

        if (getPath() != null ? !getPath().equals(that.getPath()) : that.getPath() != null) return false;
        if (getQueryParameters() != null ? !getQueryParameters().equals(that.getQueryParameters()) : that.getQueryParameters() != null)
            return false;
        if (!Arrays.equals(getPayload(), that.getPayload())) return false;
        return !(getHeaders() != null ? !getHeaders().equals(that.getHeaders()) : that.getHeaders() != null);

    }

    @Override
    public int hashCode() {
        int result = getPath() != null ? getPath().hashCode() : 0;
        result = 31 * result + (getQueryParameters() != null ? getQueryParameters().hashCode() : 0);
        result = 31 * result + (getPayload() != null ? Arrays.hashCode(getPayload()) : 0);
        result = 31 * result + (getHeaders() != null ? getHeaders().hashCode() : 0);
        return result;
    }
}
