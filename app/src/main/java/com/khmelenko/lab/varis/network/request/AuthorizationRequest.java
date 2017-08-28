package com.khmelenko.lab.varis.network.request;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Authorization request
 *
 * @author Dmytro Khmelenko
 */
public class AuthorizationRequest {

    @SerializedName("scopes")
    private final List<String> mScopes;

    @SerializedName("note")
    private final String mNote;

    @SerializedName("note_url")
    private String mNoteUrl;

    @SerializedName("client_id")
    private String mClientId;

    @SerializedName("client_secret")
    private String mClientSecret;

    @SerializedName("fingerprint")
    private String mFingerprint;

    public AuthorizationRequest(List<String> scopes, String note) {
        mScopes = new ArrayList<>(scopes);
        mNote = note;
    }

    public List<String> getScopes() {
        return mScopes;
    }

    public String getNote() {
        return mNote;
    }

    public String getNoteUrl() {
        return mNoteUrl;
    }

    public void setNoteUrl(String noteUrl) {
        mNoteUrl = noteUrl;
    }

    public String getClientId() {
        return mClientId;
    }

    public void setClientId(String clientId) {
        mClientId = clientId;
    }

    public String getClientSecret() {
        return mClientSecret;
    }

    public void setClientSecret(String clientSecret) {
        mClientSecret = clientSecret;
    }

    public String getFingerprint() {
        return mFingerprint;
    }

    public void setFingerprint(String fingerprint) {
        mFingerprint = fingerprint;
    }
}
