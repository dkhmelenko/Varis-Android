package com.khmelenko.lab.travisclient.network.response;

import com.google.gson.annotations.SerializedName;

/**
 * Dao for commits
 *
 * @author Dmytro Khmelenko
 */
public final class Commit {

    @SerializedName("id")
    private long mId;

    @SerializedName("sha")
    private String mSha;

    @SerializedName("branch")
    private String mBranch;

    @SerializedName("message")
    private String mMessage;

    @SerializedName("committed_at")
    private String mCommitedAt;

    @SerializedName("author_name")
    private String mAuthorName;

    @SerializedName("author_email")
    private String mAuthorEmail;

    @SerializedName("committer_name")
    private String mCommitterName;

    @SerializedName("committer_email")
    private String mCommitterEmail;

    @SerializedName("compare_url")
    private String mCompareUrl;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getSha() {
        return mSha;
    }

    public void setSha(String sha) {
        mSha = sha;
    }

    public String getBranch() {
        return mBranch;
    }

    public void setBranch(String branch) {
        mBranch = branch;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getCommitedAt() {
        return mCommitedAt;
    }

    public void setCommitedAt(String commitedAt) {
        mCommitedAt = commitedAt;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public void setAuthorName(String authorName) {
        mAuthorName = authorName;
    }

    public String getAuthorEmail() {
        return mAuthorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        mAuthorEmail = authorEmail;
    }

    public String getCommitterName() {
        return mCommitterName;
    }

    public void setCommitterName(String committerName) {
        mCommitterName = committerName;
    }

    public String getCommitterEmail() {
        return mCommitterEmail;
    }

    public void setCommitterEmail(String committerEmail) {
        mCommitterEmail = committerEmail;
    }

    public String getCompareUrl() {
        return mCompareUrl;
    }

    public void setCompareUrl(String compareUrl) {
        mCompareUrl = compareUrl;
    }
}
