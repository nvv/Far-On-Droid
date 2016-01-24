package com.openfarmanager.android.googledrive.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MultipartUtility {

    private static final String CRLF = "\r\n";
    private static final String CHARSET = "UTF-8";

    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 10000;

    private final HttpURLConnection mConnection;
    private final OutputStream mOutputStream;
    private final PrintWriter mWriter;
    private final String mBoundary;


    public MultipartUtility(final URL url) throws IOException {

        mBoundary = "---------------------------" + System.currentTimeMillis();

        mConnection = (HttpURLConnection) url.openConnection();
        mConnection.setConnectTimeout(CONNECT_TIMEOUT);
        mConnection.setReadTimeout(READ_TIMEOUT);
        mConnection.setRequestMethod("POST");
        mConnection.setRequestProperty("Accept-Charset", CHARSET);
        mConnection.setRequestProperty("Content-Type", "multipart/related; boundary=" + mBoundary);
        mConnection.setUseCaches(false);
        mConnection.setDoInput(true);
        mConnection.setDoOutput(true);

        mOutputStream = mConnection.getOutputStream();
        mWriter = new PrintWriter(new OutputStreamWriter(mOutputStream, CHARSET), true);
    }

    public void addFormField(final String name, final String value, final String contentType) {
        mWriter.append("--").append(mBoundary).append(CRLF)
                .append("Content-Disposition: form-data; name=\"").append(name)
                .append("\"").append(CRLF)
                .append("Content-Type: " + contentType + "; charset=").append(CHARSET)
                .append(CRLF).append(CRLF).append(value).append(CRLF);
    }

    public void addFilePart(final String fieldName, final File uploadFile, GoogleDriveWebApi.UploadListener listener)
            throws IOException {
        final String fileName = uploadFile.getName();
        mWriter.append("--").append(mBoundary).append(CRLF)
                .append("Content-Disposition: form-data; name=\"")
                .append(fieldName).append("\"; filename=\"").append(fileName)
                .append("\"").append(CRLF).append("Content-Type: ")
                .append("application/octet-stream").append(CRLF)
                .append("Content-Transfer-Encoding: binary").append(CRLF)
                .append(CRLF);

        mWriter.flush();
        mOutputStream.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);

        try {
            final byte[] buffer = new byte[512 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                mOutputStream.write(buffer, 0, bytesRead);
                listener.onProgress(0, bytesRead, 0);
            }
            mOutputStream.flush();

        } finally {
            closeStream(inputStream);
        }

        mWriter.append(CRLF);
    }

    public void addHeaderField(String name, String value) {
        mWriter.append(name).append(": ").append(value).append(CRLF);
    }

    public int doRequest() throws IOException {

        mWriter.append(CRLF).append("--").append(mBoundary).append("--")
                .append(CRLF);
        mWriter.close();

        return mConnection.getResponseCode();
    }

    public String getResponseMessage() throws IOException {
        return mConnection.getResponseMessage();
    }

    public void close() {
        closeStream(mOutputStream);
        mConnection.disconnect();
    }

    public static void closeStream(java.io.Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException ignored) {
        } catch (Exception ignored) {
        }
    }
}
