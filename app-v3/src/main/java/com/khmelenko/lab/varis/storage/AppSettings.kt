package com.khmelenko.lab.varis.storage

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

private const val ACCESS_TOKEN_KEY = "TravisAccessToken"
private const val SERVER_TYPE_KEY = "ServerTypeKey"
private const val SERVER_URL_KEY = "ServerUrlKey"

/**
 * Application settings
 *
 * @author Dmytro Khmelenko
 */
class AppSettings(context: Context) {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * Gets access token
     *
     * @return Access token
     */
    val accessToken: String
        get() = sharedPreferences.getString(ACCESS_TOKEN_KEY, "")

    /**
     * Gets server type from Settings
     *
     * @return Server type
     */
    val serverType: Int
        get() = sharedPreferences.getInt(SERVER_TYPE_KEY, 1)

    /**
     * Gets server url from Settings
     *
     * @return Server type
     */
    val serverUrl: String
        get() = sharedPreferences.getString(SERVER_URL_KEY, "")

    /**
     * Puts access token to settings
     *
     * @param accessToken Access token
     */
    fun putAccessToken(accessToken: String) {
        val editor = sharedPreferences.edit()
        editor.putString(ACCESS_TOKEN_KEY, accessToken)
        editor.apply()
    }

    /**
     * Puts server type to the settings
     *
     * @param serverType Server type
     */
    fun putServerType(serverType: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(SERVER_TYPE_KEY, serverType)
        editor.apply()
    }

    /**
     * Puts server url to the settings
     *
     * @param serverUrl Server type
     */
    fun putServerUrl(serverUrl: String) {
        val editor = sharedPreferences.edit()
        editor.putString(SERVER_URL_KEY, serverUrl)
        editor.apply()
    }
}
