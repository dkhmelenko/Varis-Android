package com.khmelenko.lab.travisclient.util;

import android.util.Base64;

/**
 * Provides util methods related with encryption/decryption
 *
 * @author Dmytro Khmelenko
 */
public final class EncryptionUtils {

    // denied constructor
    private EncryptionUtils() {

    }

    /**
     * Encodes input string to Base64
     *
     * @param input Input string
     * @return Base64 string
     */
    public static String toBase64(String input) {
        String base64Str = Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
        return base64Str;
    }

    /**
     * Decodes base64 string to original
     *
     * @param base64 Base64 string
     * @return Original string
     */
    public static String fromBase64(String base64) {
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        String origin = new String(data);
        return origin;
    }

    /**
     * Generates string for Basic authorization
     *
     * @param username Username
     * @param password Password
     * @return Basic authorization string
     */
    public static String generateBasicAuthorization(String username, String password) {
        String credentials = String.format("%1$s:%2$s", username, password);
        String basic = String.format("Basic %1$s", toBase64(credentials)).trim();
        return basic;
    }
}
