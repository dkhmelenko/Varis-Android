package com.khmelenko.lab.varis.network.response

import com.google.gson.annotations.SerializedName

/**
 * Dao for user
 *
 * @author Dmytro Khmelenko
 */
data class User(

    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("login")
    val login: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("gravatar_id")
    val gravatarId: String?,

    @SerializedName("is_syncing")
    val isSyncing: Boolean,

    @SerializedName("synced_at")
    val syncedAt: String?,

    @SerializedName("correct_scopes")
    val isCorrectScopes: Boolean,

    @SerializedName("created_at")
    val createdAt: String
)
