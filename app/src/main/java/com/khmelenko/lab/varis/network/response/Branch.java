package com.khmelenko.lab.varis.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Dao for Branch
 *
 * @author Dmytro Khmelenko
 */
public class Branch implements IBuildState {

    @SerializedName("id")
    private long mId;

    @SerializedName("repository_id")
    private long mRepositoryId;

    @SerializedName("commit_id")
    private long mCommitId;

    @SerializedName("number")
    private String mNumber;

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

    @SerializedName("pull_request")
    private boolean mPullRequest;

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public void setId(long id) {
        mId = id;
    }

    @Override
    public long getRepositoryId() {
        return mRepositoryId;
    }

    @Override
    public void setRepositoryId(long repositoryId) {
        mRepositoryId = repositoryId;
    }

    @Override
    public long getCommitId() {
        return mCommitId;
    }

    @Override
    public void setCommitId(long commitId) {
        mCommitId = commitId;
    }

    @Override
    public String getNumber() {
        return mNumber;
    }

    @Override
    public void setNumber(String number) {
        mNumber = number;
    }

    @Override
    public String getState() {
        return mState;
    }

    @Override
    public void setState(String state) {
        mState = state;
    }

    @Override
    public String getStartedAt() {
        return mStartedAt;
    }

    @Override
    public void setStartedAt(String startedAt) {
        mStartedAt = startedAt;
    }

    @Override
    public String getFinishedAt() {
        return mFinishedAt;
    }

    @Override
    public void setFinishedAt(String finishedAt) {
        mFinishedAt = finishedAt;
    }

    @Override
    public long getDuration() {
        return mDuration;
    }

    @Override
    public void setDuration(long duration) {
        mDuration = duration;
    }

    @Override
    public boolean isPullRequest() {
        return mPullRequest;
    }

    @Override
    public void setPullRequest(boolean pullRequest) {
        mPullRequest = pullRequest;
    }

    @Override
    public List<Long> getJobIds() {
        return mJobIds;
    }

    @Override
    public void setJobIds(List<Long> jobIds) {
        mJobIds = jobIds;
    }
}
