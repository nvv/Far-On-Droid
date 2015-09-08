package com.mediafire.sdk;

import java.io.File;

public interface MediaFireHasher {

    /**
     * SHA-1 hashes a String
     * @param string
     * @return null if the String cannot be hashed
     */
    String sha1(String string);

    /**
     * MD5 hashes a String
     * @param string
     * @return null if the String cannot be hashed
     */
    String md5(String string);

    /**
     * SHA-256 hashes a String
     * @param string
     * @return null if the String cannot be hashed
     */
    String sha256(String string);

    /**
     * SHA-1 hashes a String
     * @param bytes
     * @return null if the byte[] cannot be hashed
     */
    String sha1(byte[] bytes);

    /**
     * MD5 hashes a String
     * @param bytes
     * @return null if the byte[] cannot be hashed
     */
    String md5(byte[] bytes);

    /**
     * SHA-256 hashes a String
     * @param bytes
     * @return null if the byte[] cannot be hashed
     */
    String sha256(byte[] bytes);

    /**
     * SHA-1 hashes a String
     * @param file
     * @return null if the File cannot be hashed
     */
    String sha1(File file);

    /**
     * MD5 hashes a String
     * @param file
     * @return null if the File cannot be hashed
     */
    String md5(File file);

    /**
     * SHA-256 hashes a String
     * @param file
     * @return null if the File cannot be hashed
     */
    String sha256(File file);
}
