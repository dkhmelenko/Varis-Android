package com.khmelenko.lab.varis.util

import android.util.Base64
import com.khmelenko.lab.varis.util.EncryptionUtils.toBase64
import kotlin.String.Companion

/**
 * Provides util methods related with encryption/decryption
 *
 * @author Dmytro Khmelenko
 */
object EncryptionUtils {

    /**
     * Encodes input string to Base64
     *
     * @param input Input string
     * @return Base64 string
     */
    fun toBase64(input: String): String {
        return Base64.encodeToString(input.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * Decodes base64 string to original
     *
     * @param base64 Base64 string
     * @return Original string
     */
    fun fromBase64(base64: String): String {
        val data = Base64.decode(base64, Base64.DEFAULT)
        return String(data)
    }

    /**
     * Generates string for Basic authorization
     *
     * @param username Username
     * @param password Password
     * @return Basic authorization string
     */
    fun generateBasicAuthorization(username: String, password: String): String {
        val credentials = String.format("%1\$s:%2\$s", username, password)
        return String.format("Basic %1\$s", toBase64(credentials)).trim { it <= ' ' }
    }
}
