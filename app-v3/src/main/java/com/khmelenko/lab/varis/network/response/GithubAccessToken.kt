package com.khmelenko.lab.varis.network.response

import com.google.gson.annotations.SerializedName

/**
 * DAO for Access token from Github
 *
 * @author Dmytro Khmelenko
 */
data class GithubAccessToken(

        @SerializedName("access_token")
        val accessToken: String,

        @SerializedName("scope")
        val scope: String,

        @SerializedName("token_type")
        val tokenType: String
)
