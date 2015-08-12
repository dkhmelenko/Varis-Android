package com.khmelenko.lab.travisclient.network.request;

import com.google.gson.annotations.SerializedName;

/**
 * Defines the request for getting Travis access token
 *
 * @author Dmytro Khmelenko
 */
public final class AccessTokenRequest {

    @SerializedName("github_token")
    private String mGithubToken;

    public String getGithubToken() {
        return mGithubToken;
    }

    public void setGithubToken(String githubToken) {
        mGithubToken = githubToken;
    }
}
