package com.khmelenko.lab.varis.network.response

import com.google.gson.annotations.SerializedName

/**
 * Dao the Requests response
 *
 * @author Dmytro Khmelenko
 */
data class Requests (

    @SerializedName("requests")
    val requests: List<RequestData>,

    @SerializedName("commits")
    val commits: List<Commit>,

    @Transient
    var builds: List<Build>
)
