package com.mediafire.sdk.util;

public final class TextUtils {

    private TextUtils() {

    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }
}
