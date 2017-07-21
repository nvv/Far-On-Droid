package com.openfarmanager.android.utils;

import java.text.DecimalFormat;

/**
 * @author vnamashko
 */
public class CustomFormatter {

    public  static DecimalFormat format;
    public  static final long divisor = 1024;
    public static final String suffix = "B";
    public  static final String nbsp = "&nbsp;";
    public  static final String[] scale = {nbsp, "K","M","G","T",};

    static {
        format = (DecimalFormat) DecimalFormat.getNumberInstance();
        format.applyPattern("#,###.##");
    }

    /**
     * Gets formatted value (human readable string).
     *
     * @param value as long
     * @return value as string
     */
    public static String formatBytes(long value) {
        float scaledValue = 0;
        String scaleSuffix = scale[0];
        if (value != 0) {
            for (int i = scale.length - 1; i >= 0; i--) {
                long div = (long) Math.pow(divisor, i);
                if (value >= div) {
                    scaledValue = (float) (1.0 * value / div);
                    scaleSuffix = scale[i];
                    break;
                }
            }
        }
        StringBuilder sb = new StringBuilder(3);
        sb.append(format.format(scaledValue));

        sb.append(" ");
        if (!scaleSuffix.equals(scale[0])) {
            sb.append(scaleSuffix);
        }

        sb.append(suffix);
        return sb.toString();
    }

    /**
     * @param size file size number
     * @param unit {0, 1, 2} corresponds to {KB, MB, GB}
     *
     * @return file size in bytes
     */
    public static long convertToBytes(int size, int unit) {
        long s = size;
        for (int i = unit + 1; i >= 1; i--) {
            s *= divisor;
        }

        return s;
    }

    /**
     * Convert formatted (human readable string) to bytes.
     *
     * @param text human readable string
     * @return size in bytes
     */
    public static long parseSize(String text) {

        double d = Double.parseDouble(text.replaceAll("bytes|[GMK]B$", ""));
        long l = Math.round(d * 1024 * 1024 * 1024L);
        switch (text.charAt(Math.max(0, text.length() - 2))) {
            default:  l /= 1024;
            case 'K': l /= 1024;
            case 'M': l /= 1024;
            case 'G': return l;
        }
    }

}
