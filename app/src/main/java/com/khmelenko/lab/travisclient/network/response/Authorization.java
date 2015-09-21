package com.khmelenko.lab.travisclient.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Authorization response
 *
 * @author Dmytro Khmelenko
 */
public final class Authorization {

    @SerializedName("id")
    private long mId;

    @SerializedName("url")
    private String mUrl;

    @SerializedName("scopes")
    private List<String> mScopes;

    @SerializedName("token")
    private String mToken;

    @SerializedName("token_last_eight")
    private String mTokenLastEight;

    @SerializedName("hashed_token")
    private String mHashedToken;

    @SerializedName("app")
    private App mApp;

    @SerializedName("note")
    private String mNote;

    @SerializedName("note_url")
    private String mNoteUrl;

    @SerializedName("updated_at")
    private String mUpdatedAt;

    @SerializedName("created_at")
    private String mCreatedAt;

    @SerializedName("fingerprint")
    private String mFingerprint;


    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public List<String> getScopes() {
        return mScopes;
    }

    public void setScopes(List<String> scopes) {
        mScopes = scopes;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getTokenLastEight() {
        return mTokenLastEight;
    }

    public void setTokenLastEight(String tokenLastEight) {
        mTokenLastEight = tokenLastEight;
    }

    public String getHashedToken() {
        return mHashedToken;
    }

    public void setHashedToken(String hashedToken) {
        mHashedToken = hashedToken;
    }

    public App getApp() {
        return mApp;
    }

    public void setApp(App app) {
        mApp = app;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }

    public String getNoteUrl() {
        return mNoteUrl;
    }

    public void setNoteUrl(String noteUrl) {
        mNoteUrl = noteUrl;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public String getFingerprint() {
        return mFingerprint;
    }

    public void setFingerprint(String fingerprint) {
        mFingerprint = fingerprint;
    }

    public class App {

        @SerializedName("url")
        private String mUrl;

        @SerializedName("name")
        private String mName;

        @SerializedName("client_id")
        private String mClientId;

        public String getUrl() {
            return mUrl;
        }

        public void setUrl(String url) {
            mUrl = url;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getClientId() {
            return mClientId;
        }

        public void setClientId(String clientId) {
            mClientId = clientId;
        }
    }
}
