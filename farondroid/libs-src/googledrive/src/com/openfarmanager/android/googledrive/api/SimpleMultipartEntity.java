package com.openfarmanager.android.googledrive.api;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

/**
 * author: Vlad Namashko
 */
public class SimpleMultipartEntity implements HttpEntity {

    private final static char[] MULTIPART_CHARS =
            "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private String mBoundary = null;

    ByteArrayOutputStream mOut = new ByteArrayOutputStream();
    boolean mIsSetLast = false;
    boolean mIsSetFirst = false;

    private GoogleDriveWebApi.UploadListener mUploadListener;

    public SimpleMultipartEntity() {
        final StringBuilder buf = new StringBuilder();
        final Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        mBoundary = buf.toString();
    }

    public SimpleMultipartEntity(String boundary, GoogleDriveWebApi.UploadListener listener) {
        this();
        mBoundary = boundary;
        mUploadListener = listener;
    }

    public void writeFirstBoundaryIfNeeds() {
        if (!mIsSetFirst) {
            try {
                mOut.write(("--" + mBoundary + "\r\n").getBytes());
            } catch (final IOException e) {
                Log.e("SimpleMultipartEntity", e.getMessage(), e);
            }
        }
        mIsSetFirst = true;
    }

    public void writeLastBoundaryIfNeeds() {
        if (mIsSetLast) {
            return;
        }
        try {
            mOut.write(("\r\n--" + mBoundary + "--\r\n").getBytes());
        } catch (final IOException e) {
            Log.e("SimpleMultipartEntity", e.getMessage(), e);
        }
        mIsSetLast = true;
    }

    public void addPart(final String key, final String value) {
        writeFirstBoundaryIfNeeds();
        try {
            mOut.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n").getBytes());
            mOut.write("Content-Type: application/json; charset=utf-8\r\n".getBytes());
            mOut.write("Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes());
            mOut.write(value.getBytes());
            mOut.write(("\r\n--" + mBoundary + "\r\n").getBytes());
        } catch (final IOException e) {
            Log.e("SimpleMultipartEntity", e.getMessage(), e);
        }
    }

    public void addPart(final String key, final String fileName, final InputStream fin) {
        addPart(key, fileName, fin, "application/octet-stream");
    }

    public void addPart(final String key, final String fileName, final InputStream fin, String type) {
        writeFirstBoundaryIfNeeds();
        try {
            type = "Content-Type: " + type + "\r\n";
            mOut.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"\r\n").getBytes());
            mOut.write(type.getBytes());
            mOut.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());

            final byte[] tmp = new byte[4096];
            int l = 0;
            while ((l = fin.read(tmp)) != -1) {
                mOut.write(tmp, 0, l);
            }
            mOut.flush();
        } catch (final IOException e) {
            Log.e("SimpleMultipartEntity", e.getMessage(), e);
        } finally {
            try {
                fin.close();
            } catch (final IOException e) {
                Log.e("SimpleMultipartEntity", e.getMessage(), e);
            }
        }
    }

    public void addPart(final String key, final File value) {
        try {
            addPart(key, value.getName(), new FileInputStream(value));
        } catch (final FileNotFoundException e) {
            Log.e("SimpleMultipartEntity", e.getMessage(), e);
        }
    }

    public void reset() {
        try {
            mOut.reset();
            mOut.close();
        } catch (Exception ignore) {}
    }

    @Override
    public long getContentLength() {
        writeLastBoundaryIfNeeds();
        return mOut.toByteArray().length;
    }

    @Override
    public Header getContentType() {
        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + mBoundary);
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {

        int size = 16 * 1024;
        byte[] bytes = mOut.toByteArray();
        byte[] buffer = new byte[size];

        int totalLen = bytes.length;
        int i = 0;
        while (i < bytes.length) {

            Arrays.fill(buffer, (byte) 0);
            int portionToTransfer = (totalLen - i) >= size ? size : (totalLen - i);
            System.arraycopy(bytes, i, buffer, 0, portionToTransfer);
            i += size;
            //System.out.println("::::::::  " + i);
            mUploadListener.onProgress(i, portionToTransfer, totalLen);
            outstream.write(buffer);
        }
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public void consumeContent() throws IOException,
            UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException(
                    "Streaming entity does not implement #consumeContent()");
        }
    }

    @Override
    public InputStream getContent() throws IOException,
            UnsupportedOperationException {
        return new ByteArrayInputStream(mOut.toByteArray());
    }

}
