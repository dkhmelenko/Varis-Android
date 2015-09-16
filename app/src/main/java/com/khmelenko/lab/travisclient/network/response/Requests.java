package com.khmelenko.lab.travisclient.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Dao the Requests response
 *
 * @author Dmytro Khmelenko
 */
public class Requests {

    @SerializedName("requests")
    private List<RequestData> mRequests;

    @SerializedName("commits")
    private List<Commit> mCommits;

    public List<RequestData> getRequests() {
        return mRequests;
    }

    public void setRequests(List<RequestData> requests) {
        mRequests = requests;
    }

    public List<Commit> getCommits() {
        return mCommits;
    }

    public void setCommits(List<Commit> commits) {
        mCommits = commits;
    }
}
