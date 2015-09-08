package com.mediafire.sdk;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MFHttpRequester implements MediaFireHttpRequester {
    private final MediaFireHttpsAgent httpsAgent;
    private final int connectionTimeout;
    private final int readTimeout;

    public MFHttpRequester(MediaFireHttpsAgent httpsAgent, int connectionTimeout, int readTimeout) {
        this.httpsAgent = httpsAgent;
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    public MFHttpRequester(int connectionTimeout, int readTimeout) {
        this(null, connectionTimeout, readTimeout);
    }

    @Override
    public MediaFireHttpResponse get(MediaFireHttpRequest request) throws MediaFireException {
        return makeRequest(request, false);
    }

    @Override
    public MediaFireHttpResponse post(MediaFireHttpRequest request) throws MediaFireException {
        return makeRequest(request, true);
    }

    @Override
    public MediaFireHttpsAgent getHttpsAgent() {
        return httpsAgent;
    }

    private MediaFireHttpResponse makeRequest(MediaFireHttpRequest request, boolean doOutput) throws MediaFireException {
        try {
            String urlString = request.getRequestUrl();
            Map<String, Object> headers = request.getRequestHeaders();
            byte[] payload = request.getRequestPayload();

            HttpsURLConnection connection = createHttpsUrlConnection(urlString);

            setupConnection(connection, headers, doOutput);

            if (doOutput && payload != null) {
                connection.getOutputStream().write(payload);
            }

            MediaFireHttpResponse response = getResponse(connection);
            return response;
        } catch (IOException e) {
            throw new MediaFireException("IO exception: ", e);
        }
    }

    private HttpsURLConnection createHttpsUrlConnection(String url) throws MediaFireException {
        try {
            return (HttpsURLConnection) new URL(url).openConnection();
        } catch (MalformedURLException e) {
            throw new MediaFireException("bad url: " + url, e);
        } catch (IOException e) {
            throw new MediaFireException("IO exception: ", e);
        }
    }

    private void setupConnection(HttpsURLConnection connection, Map<String, Object> headers, boolean doOutput) {

        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(readTimeout);

        connection.setDoOutput(doOutput);

        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet()) {
                if (headers.get(key) != null) {
                    connection.addRequestProperty(key, String.valueOf(headers.get(key)));
                }
            }
        }

        if (getHttpsAgent() != null) {
            getHttpsAgent().configureHttpsUrlConnection(connection);
        }
    }

    private MediaFireHttpResponse getResponse(HttpsURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        InputStream inputStream;
        inputStream = responseCode / 100 != 2 ? connection.getErrorStream() : connection.getInputStream();
        byte[] response = readStream(inputStream);

        if (inputStream != null) {
            inputStream.close();
        }
        Map<String, List<String>> headerFields = connection.getHeaderFields();

        return new MFHttpResponse(responseCode, response, headerFields);
    }

    private byte[] readStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        byte[] buffer = new byte[1024];
        int count;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        while ((count = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, count);
        }

        byte[] bytes = outputStream.toByteArray();
        outputStream.close();
        return bytes;
    }
}
