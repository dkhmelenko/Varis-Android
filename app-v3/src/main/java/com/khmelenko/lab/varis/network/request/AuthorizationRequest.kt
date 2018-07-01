package com.khmelenko.lab.varis.network.request

import com.google.gson.annotations.SerializedName

/**
 * Authorization request
 *
 * @author Dmytro Khmelenko
 */
data class AuthorizationRequest(
        @SerializedName("scopes") val scopes: List<String>,
        @SerializedName("note") val note: String,
        @SerializedName("note_url") val noteUrl: String? = null,
        @SerializedName("client_id") val clientId: String? = null,
        @SerializedName("client_secret") val clientSecret: String? = null,
        @SerializedName("fingerprint") val fingerprint: String? = null
)
