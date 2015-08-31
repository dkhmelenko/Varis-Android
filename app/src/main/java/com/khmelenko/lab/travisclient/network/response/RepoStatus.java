package com.khmelenko.lab.travisclient.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Repository status
 *
 * @author Dmytro Khmelenko
 */
public class RepoStatus {

    @SerializedName("builds")
    private List<Build> mBuilds;

    @SerializedName("commits")
    private List<Commit> mCommits;

    // TODO
//    @SerializedName("jobs")
//    private List<Job> mJobs;
}
