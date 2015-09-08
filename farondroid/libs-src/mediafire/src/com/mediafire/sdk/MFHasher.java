package com.mediafire.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MFHasher implements MediaFireHasher {

    private static final String MD5 = "MD5";
    private static final String SHA1 = "SHA-1";
    private static final String SHA256 = "SHA-256";

    public MFHasher() {
    }

    @Override
    public String sha1(String string) {
        if (string == null) {
            return null;
        }
        try {
            return hash(SHA1, string.getBytes());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    @Override
    public String md5(String string) {
        if (string == null) {
            return null;
        }
        try {
            return hash(MD5, string.getBytes());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    @Override
    public String sha256(String string) {
        if (string == null) {
            return null;
        }
        try {
            return hash(SHA256, string.getBytes());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    @Override
    public String sha1(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return hash(SHA1, bytes);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    @Override
    public String md5(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return hash(MD5, bytes);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    @Override
    public String sha256(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return hash(SHA256, bytes);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    @Override
    public String sha1(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            return hash(SHA1, file);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String md5(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            return hash(MD5, file);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String sha256(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            return hash(SHA256, file);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private String hash(String algorithm, byte[] bytesToHash) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(bytesToHash);

        byte bytes[] = md.digest();

        return buildHashString(bytes);
    }

    private String hash(String algorithm, File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        FileInputStream fis = new FileInputStream(file);

        byte[] inputBytes = new byte[1024];

        int readBytes;
        while ((readBytes = fis.read(inputBytes)) != -1) {
            md.update(inputBytes, 0, readBytes);
        }

        byte[] bytes = md.digest();

        return buildHashString(bytes);
    }

    private String buildHashString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
