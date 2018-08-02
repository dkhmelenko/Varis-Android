package com.khmelenko.lab.varis.network.response

import com.google.gson.annotations.SerializedName

/**
 * Travis access token
 *
 * @author Dmytro Khmelenko
 */
data class AccessToken(
        @SerializedName("access_token")
        val accessToken: String
)
