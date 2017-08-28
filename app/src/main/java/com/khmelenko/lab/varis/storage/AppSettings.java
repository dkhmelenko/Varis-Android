package com.khmelenko.lab.varis.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Application settings
 *
 * @author Dmytro Khmelenko
 */
public class AppSettings {

    private static final String ACCESS_TOKEN_KEY = "TravisAccessToken";
    private static final String SERVER_TYPE_KEY = "ServerTypeKey";
    private static final String SERVER_URL_KEY = "ServerUrlKey";

    private SharedPreferences mSharedPreferences;

    public AppSettings(Context context) {
        mSharedPreferences = getPreferences(context);
    }

    /**
     * Gets shared preferences
     *
     * @return Shared preferences
     */
    private SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Gets access token
     *
     * @return Access token
     */
    public String getAccessToken() {
        return mSharedPreferences.getString(ACCESS_TOKEN_KEY, "");
    }

    /**
     * Puts access token to settings
     *
     * @param accessToken Access token
     */
    public void putAccessToken(String accessToken) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ACCESS_TOKEN_KEY, accessToken);
        editor.apply();
    }

    /**
     * Gets server type from Settings
     *
     * @return Server type
     */
    public int getServerType() {
        return mSharedPreferences.getInt(SERVER_TYPE_KEY, 1);
    }

    /**
     * Puts server type to the settings
     *
     * @param serverType Server type
     */
    public void putServerType(int serverType) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(SERVER_TYPE_KEY, serverType);
        editor.apply();
    }

    /**
     * Gets server url from Settings
     *
     * @return Server type
     */
    public String getServerUrl() {
        return mSharedPreferences.getString(SERVER_URL_KEY, "");
    }

    /**
     * Puts server url to the settings
     *
     * @param serverUrl Server type
     */
    public void putServerUrl(String serverUrl) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(SERVER_URL_KEY, serverUrl);
        editor.apply();
    }

}
