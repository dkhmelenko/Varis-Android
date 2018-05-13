package com.khmelenko.lab.varis.network.response;

import com.google.gson.annotations.SerializedName;

/**
 * Travis access token
 *
 * @author Dmytro Khmelenko
 */
public class AccessToken {

    @SerializedName("access_token")
    private String mAccessToken;

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }
}
