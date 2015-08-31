package com.khmelenko.lab.travisclient.event.travis;

import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.network.response.RepoStatus;

import java.util.List;

/**
 * Event on completed loading repository status
 *
 * @author Dmytro Khmelenko
 */
public class RepoStatusLoadedEvent {

    private final RepoStatus mRepoStatus;

    public RepoStatusLoadedEvent(RepoStatus repoStatus) {
        mRepoStatus = repoStatus;
    }

    public RepoStatus getRepoStatus() {
        return mRepoStatus;
    }
}
