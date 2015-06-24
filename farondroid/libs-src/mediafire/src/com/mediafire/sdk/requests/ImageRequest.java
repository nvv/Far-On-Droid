package com.mediafire.sdk.requests;

/**
 * Request used to fetch an image from mediafire
 */
public class ImageRequest {

    private final String hash;
    private final String quickKey;
    private final char sizeId;
    private final boolean conversionOnly;

    public ImageRequest(String hash, String quickKey, char sizeId, boolean conversionOnly) {
        this.hash = hash;
        this.quickKey = quickKey;
        this.sizeId = sizeId;
        this.conversionOnly = conversionOnly;
    }

    public ImageRequest(String hash, String quickKey, char sizeId) {
        this(hash, quickKey, sizeId, false);
    }

    public String getHash() {
        return hash;
    }

    public String getQuickKey() {
        return quickKey;
    }

    public char getSizeId() {
        return sizeId;
    }

    public boolean isConversionOnly() {
        return conversionOnly;
    }
}
