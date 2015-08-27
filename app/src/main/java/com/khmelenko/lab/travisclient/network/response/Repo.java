package com.khmelenko.lab.travisclient.network.response;

import com.google.gson.annotations.SerializedName;

/**
 * DAO for Repository
 *
 * @author Dmytro Khmelenko
 */
public class Repo {

    @SerializedName("id")
    private String mId;

    @SerializedName("slug")
    private String mSlug;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("last_build_id")
    private String mLastBuildId;

    @SerializedName("last_build_number")
    private String mLastBuildNumber;

    @SerializedName("last_build_state")
    private String mLastBuildState;

    @SerializedName("last_build_duration")
    private long mLastBuildDuration;

    @SerializedName("last_build_started_at")
    private String mLastBuildStartedAt;

    @SerializedName("last_build_finished_at")
    private String mLastBuildFinishedAt;

    @SerializedName("github_language")
    private String mGithubLanguage;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        mSlug = slug;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getLastBuildId() {
        return mLastBuildId;
    }

    public void setLastBuildId(String lastBuildId) {
        mLastBuildId = lastBuildId;
    }

    public String getLastBuildNumber() {
        return mLastBuildNumber;
    }

    public void setLastBuildNumber(String lastBuildNumber) {
        mLastBuildNumber = lastBuildNumber;
    }

    public String getLastBuildState() {
        return mLastBuildState;
    }

    public void setLastBuildState(String lastBuildState) {
        mLastBuildState = lastBuildState;
    }

    public long getLastBuildDuration() {
        return mLastBuildDuration;
    }

    public void setLastBuildDuration(long lastBuildDuration) {
        mLastBuildDuration = lastBuildDuration;
    }

    public String getLastBuildStartedAt() {
        return mLastBuildStartedAt;
    }

    public void setLastBuildStartedAt(String lastBuildStartedAt) {
        mLastBuildStartedAt = lastBuildStartedAt;
    }

    public String getLastBuildFinishedAt() {
        return mLastBuildFinishedAt;
    }

    public void setLastBuildFinishedAt(String lastBuildFinishedAt) {
        mLastBuildFinishedAt = lastBuildFinishedAt;
    }

    public String getGithubLanguage() {
        return mGithubLanguage;
    }

    public void setGithubLanguage(String githubLanguage) {
        mGithubLanguage = githubLanguage;
    }
}
