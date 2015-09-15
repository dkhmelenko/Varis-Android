package com.khmelenko.lab.travisclient.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Dao for Build
 *
 * @author Dmytro Khmelenko
 */
public class Build {

    @SerializedName("id")
    private long mId;

    @SerializedName("repository_id")
    private long mRepositoryId;

    @SerializedName("commit_id")
    private long mCommitId;

    @SerializedName("number")
    private String mNumber;

    @SerializedName("pull_request")
    private boolean mPullRequest;

    @SerializedName("pull_request_number")
    private String mPullRequestNumber;

    @SerializedName("pull_request_title")
    private String mPullRequestTitle;

//    private Config mConfig;

    @SerializedName("state")
    private String mState;

    @SerializedName("started_at")
    private String mStartedAt;

    @SerializedName("finished_at")
    private String mFinishedAt;

    @SerializedName("duration")
    private long mDuration;

    @SerializedName("job_ids")
    private List<Long> mJobIds;


    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getRepositoryId() {
        return mRepositoryId;
    }

    public void setRepositoryId(long repositoryId) {
        mRepositoryId = repositoryId;
    }

    public long getCommitId() {
        return mCommitId;
    }

    public void setCommitId(long commitId) {
        mCommitId = commitId;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public boolean isPullRequest() {
        return mPullRequest;
    }

    public void setPullRequest(boolean pullRequest) {
        mPullRequest = pullRequest;
    }

    public String getPullRequestNumber() {
        return mPullRequestNumber;
    }

    public void setPullRequestNumber(String pullRequestNumber) {
        mPullRequestNumber = pullRequestNumber;
    }

    public String getPullRequestTitle() {
        return mPullRequestTitle;
    }

    public void setPullRequestTitle(String pullRequestTitle) {
        mPullRequestTitle = pullRequestTitle;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getStartedAt() {
        return mStartedAt;
    }

    public void setStartedAt(String startedAt) {
        mStartedAt = startedAt;
    }

    public String getFinishedAt() {
        return mFinishedAt;
    }

    public void setFinishedAt(String finishedAt) {
        mFinishedAt = finishedAt;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public List<Long> getJobIds() {
        return mJobIds;
    }

    public void setJobIds(List<Long> jobIds) {
        mJobIds = jobIds;
    }
}
