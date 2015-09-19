package com.khmelenko.lab.travisclient.util;

import java.util.Random;

/**
 * Provides utils methods for the work with Strings
 *
 * @author Dmytro Khmelenko
 */
public final class StringUtils {

    // denied constructor
    private StringUtils() {

    }

    /**
     * Gets random string
     *
     * @return Random string
     */
    public static String getRandomString() {
        final int length = 10;
        return getRandomString(length);
    }

    /**
     * Gets random string
     *
     * @param length String length
     * @return Random string
     */
    public static String getRandomString(int length) {
        char[] chars = "ABCDEFGHIJKLMONPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            builder.append(c);
        }
        return builder.toString();
    }
}
