package com.mediafire.sdk.util;

import com.mediafire.sdk.MFRuntimeException;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    private static final String MD5 = "MD5";
    private static final String SHA1 = "SHA-1";
    private static final String SHA256 = "SHA-256";

    private HashUtil() {
        // no instantiation, utility class
    }

    public static String sha1(String string) {
        return hash(SHA1, string.getBytes());
    }

    public static String md5(String string) {
        return hash(MD5, string.getBytes());
    }

    public static String sha256(String string) {
        return hash(SHA256, string.getBytes());
    }

    public static String sha1(byte[] bytes) {
        return hash(SHA1, bytes);
    }

    public static String md5(byte[] bytes) {
        return hash(MD5, bytes);
    }

    public static String sha256(byte[] bytes) {
        return hash(SHA256, bytes);
    }

    public static String sha1(File file) throws IOException {
        return hash(SHA1, file);
    }

    public static String md5(File file) throws IOException {
        return hash(MD5, file);
    }
    
    public static String sha256(File file) throws IOException {
        return hash(SHA256, file);
    }

    private static String hash(String algorithm, byte[] bytesToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(bytesToHash);

            byte bytes[] = md.digest();

            return buildHashString(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new MFRuntimeException("SDK internal error while using HashUtil with algorithm " + algorithm, e);
        }
    }

    private static String hash(String algorithm, File file) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            FileInputStream fis = new FileInputStream(file);

            byte[] inputBytes = new byte[1024];

            int readBytes;
            while ((readBytes = fis.read(inputBytes)) != -1) {
                md.update(inputBytes, 0, readBytes);
            }

            byte[] bytes = md.digest();

            return buildHashString(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new MFRuntimeException("SDK internal error while using HashUtil with algorithm " + algorithm, e);
        }
    }

    private static String buildHashString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
