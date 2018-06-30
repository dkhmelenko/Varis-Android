package com.khmelenko.lab.varis.network.request

import com.google.gson.annotations.SerializedName

/**
 * Defines the request for getting Travis access token
 *
 * @author Dmytro Khmelenko
 */
data class AccessTokenRequest(@SerializedName("github_token") var gitHubToken: String? = null)
