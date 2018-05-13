package com.khmelenko.lab.varis.network.request;

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AccessTokenRequest))
            return false;
        if (o == this)
            return true;

        AccessTokenRequest that = (AccessTokenRequest) o;
        return mGithubToken.equals(that.mGithubToken);
    }

    @Override
    public int hashCode() {
        return mGithubToken.hashCode();
    }
}
