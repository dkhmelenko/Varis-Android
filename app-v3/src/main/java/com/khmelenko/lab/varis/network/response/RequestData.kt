package com.khmelenko.lab.varis.network.response

import com.google.gson.annotations.SerializedName

/**
 * Dao for Request
 *
 * @author Dmytro Khmelenko
 */
data class RequestData (

    @SerializedName("id")
    val id: Long = 0,

    @SerializedName("commit_id")
    val commitId: Long = 0,

    @SerializedName("repository_id")
    val repositoryId: Long = 0,

    @SerializedName("created_at")
    val created: String? = null,

    @SerializedName("owner_id")
    val ownerId: Long = 0,

    @SerializedName("owner_type")
    val ownerType: String? = null,

    @SerializedName("event_type")
    val eventType: String? = null,

    @SerializedName("base_commit")
    val baseCommit: String? = null,

    @SerializedName("head_commit")
    val headCommit: String? = null,

    @SerializedName("result")
    val result: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("pull_request")
    val isPullRequest: Boolean = false,

    @SerializedName("pull_request_number")
    val pullRequestNumber: String? = null,

    @SerializedName("pull_request_title")
    val pullRequestTitle: String? = null,

    @SerializedName("branch")
    val branch: String? = null,

    @SerializedName("tag")
    val tag: String? = null,

    @SerializedName("build_id")
    val buildId: Long = 0
)
