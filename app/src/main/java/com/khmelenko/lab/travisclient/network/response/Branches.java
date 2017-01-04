package com.khmelenko.lab.travisclient.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * Repository branches
 *
 * @author Dmytro Khmelenko
 */
public final class Branches {

    @SerializedName("branches")
    private List<Branch> mBranches;

    @SerializedName("commits")
    private List<Commit> mCommits;

    // TODO
//    @SerializedName("jobs")
//    private List<Job> mJobs;


    public List<Branch> getBranches() {
        return Collections.unmodifiableList(mBranches);
    }

    public List<Commit> getCommits() {
        return Collections.unmodifiableList(mCommits);
    }

    public void setBranches(List<Branch> branches) {
        mBranches = branches;
    }

    public void setCommits(List<Commit> commits) {
        mCommits = commits;
    }
}
