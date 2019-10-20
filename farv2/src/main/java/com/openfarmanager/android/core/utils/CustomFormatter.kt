package com.openfarmanager.android.core.utils

import java.text.DecimalFormat

/**
 * @author vnamashko
 */
object CustomFormatter {

    var format: DecimalFormat
    val divisor: Long = 1024
    val suffix = "B"
    val nbsp = "&nbsp;"
    val scale = arrayOf(nbsp, "K", "M", "G", "T")

    init {
        format = DecimalFormat.getNumberInstance() as DecimalFormat
        format.applyPattern("#,###.##")
    }

    /**
     * Gets formatted value (human readable string).
     *
     * @param value as long
     * @return value as string
     */
    fun formatBytes(value: Long): String {
        var scaledValue = 0f
        var scaleSuffix = scale[0]
        if (value != 0L) {
            for (i in scale.indices.reversed()) {
                val div = Math.pow(divisor.toDouble(), i.toDouble()).toLong()
                if (value >= div) {
                    scaledValue = (1.0 * value / div).toFloat()
                    scaleSuffix = scale[i]
                    break
                }
            }
        }
        val sb = StringBuilder(3)
        sb.append(format.format(scaledValue.toDouble()))

        sb.append(" ")
        if (scaleSuffix != scale[0]) {
            sb.append(scaleSuffix)
        }

        sb.append(suffix)
        return sb.toString()
    }

    /**
     * @param size file size number
     * @param unit {0, 1, 2} corresponds to {KB, MB, GB}
     *
     * @return file size in bytes
     */
    fun convertToBytes(size: Int, unit: Int): Long {
        var s = size.toLong()
        for (i in unit + 1 downTo 1) {
            s *= divisor
        }

        return s
    }

    /**
     * Convert formatted (human readable string) to bytes.
     *
     * @param text human readable string
     * @return size in bytes
     */
    fun parseSize(text: String): Long {

        val d = java.lang.Double.parseDouble(text.replace("bytes|[GMK]B$".toRegex(), ""))
        var l = Math.round(d * 1024.0 * 1024.0 * 1024.0)
        when (text[Math.max(0, text.length - 2)]) {
            'K' -> {
                l /= 1024
                l /= 1024
                return l
            }
            'M' -> {
                l /= 1024
                return l
            }
            'G' -> return l
            else -> {
                l /= 1024
                l /= 1024
                l /= 1024
                return l
            }
        }
    }

}
