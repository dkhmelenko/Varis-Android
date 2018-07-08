package com.khmelenko.lab.varis.util

import java.util.Random

/**
 * Provides utils methods for the work with Strings
 *
 * @author Dmytro Khmelenko
 */
object StringUtils {

    /**
     * Gets random string
     *
     * @return Random string
     */
    @JvmStatic
    fun getRandomString(): String {
        val length = 10
        return getRandomString(length)
    }

    /**
     * Gets random string
     *
     * @param length String length
     * @return Random string
     */
    @JvmStatic
    fun getRandomString(length: Int): String {
        val chars = "ABCDEFGHIJKLMONPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray()
        val builder = StringBuilder()
        val random = Random()
        for (i in 0 until length) {
            val c = chars[random.nextInt(chars.size)]
            builder.append(c)
        }
        return builder.toString()
    }

    /**
     * Checks whether the string is null or not
     *
     * @param string String for checking
     * @return True if string is empty. False otherwise
     */
    @JvmStatic
    fun isEmpty(string: String?): Boolean {
        return string == null || string.isEmpty()
    }
}
