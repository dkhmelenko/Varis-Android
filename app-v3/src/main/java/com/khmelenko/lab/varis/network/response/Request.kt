package com.khmelenko.lab.varis.network.response

import com.google.gson.annotations.SerializedName

/**
 * Dao for Request response
 *
 * @author Dmytro Khmelenko
 */
data class Request (

    @SerializedName("request")
    val requestData: RequestData? = null,

    @SerializedName("commit")
    val commit: Commit? = null
)
