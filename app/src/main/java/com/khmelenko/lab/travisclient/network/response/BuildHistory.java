package com.khmelenko.lab.travisclient.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * Build history
 *
 * @author Dmytro Khmelenko
 */
public class BuildHistory {

    @SerializedName("builds")
    private List<Build> mBuilds;

    @SerializedName("commits")
    private List<Commit> mCommits;


    public List<Build> getBuilds() {
        return Collections.unmodifiableList(mBuilds);
    }

    public List<Commit> getCommits() {
        return Collections.unmodifiableList(mCommits);
    }
}
