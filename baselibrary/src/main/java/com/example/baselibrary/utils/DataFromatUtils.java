package com.example.baselibrary.utils;

/**
 * Created by guobiao on 2018/7/13.
 * 数据格式化
 */

public class DataFromatUtils {
    private static final String HEXES = "0123456789ABCDEF";

    public static String byteArrayToHexString(final byte[] array) {
        final StringBuilder sb = new StringBuilder();
        boolean firstEntry = true;
        sb.append('[');

        for (final byte b : array) {
            if (!firstEntry) {
                sb.append(", ");
            }
            sb.append(HEXES.charAt((b & 0xF0) >> 4));
            sb.append(HEXES.charAt((b & 0x0F)));
            firstEntry = false;
        }

        sb.append(']');
        return sb.toString();
    }
}
