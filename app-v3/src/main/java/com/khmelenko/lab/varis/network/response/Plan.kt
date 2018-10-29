package com.khmelenko.lab.varis.network.response

import com.google.gson.annotations.SerializedName

data class Plan(
        @SerializedName("name") var name: String,
        @SerializedName("space") var space: Int,
        @SerializedName("collaborators") var collaborators: Int,
        @SerializedName("private_repos") var privateRepos: Int
)