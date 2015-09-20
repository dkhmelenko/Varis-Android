package com.khmelenko.lab.travisclient.network.response;

import com.google.gson.annotations.SerializedName;

/**
 * Dao for user
 *
 * @author Dmytro Khmelenko
 */
public class User {

    @SerializedName("id")
    private long mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("login")
    private String mLogin;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("gravatar_id")
    private String mGravatarId;

    @SerializedName("is_syncing")
    private boolean mSyncing;

    @SerializedName("synced_at")
    private String mSyncedAt;

    @SerializedName("correct_scopes")
    private boolean mCorrectScopes;

    @SerializedName("created_at")
    private String mCreatedAt;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getLogin() {
        return mLogin;
    }

    public void setLogin(String login) {
        mLogin = login;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getGravatarId() {
        return mGravatarId;
    }

    public void setGravatarId(String gravatarId) {
        mGravatarId = gravatarId;
    }

    public boolean isSyncing() {
        return mSyncing;
    }

    public void setSyncing(boolean syncing) {
        mSyncing = syncing;
    }

    public String getSyncedAt() {
        return mSyncedAt;
    }

    public void setSyncedAt(String syncedAt) {
        mSyncedAt = syncedAt;
    }

    public boolean isCorrectScopes() {
        return mCorrectScopes;
    }

    public void setCorrectScopes(boolean correctScopes) {
        mCorrectScopes = correctScopes;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }
}
