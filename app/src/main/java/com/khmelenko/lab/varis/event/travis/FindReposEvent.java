package com.khmelenko.lab.varis.event.travis;

import com.khmelenko.lab.varis.network.response.Repo;

import java.util.List;

/**
 * Event on completed search of repositories
 *
 * @author Dmytro Khmelenko
 */
public final class FindReposEvent {

    private final List<Repo> mRepos;

    public FindReposEvent(List<Repo> repos) {
        mRepos = repos;
    }

    public List<Repo> getRepos() {
        return mRepos;
    }
}
