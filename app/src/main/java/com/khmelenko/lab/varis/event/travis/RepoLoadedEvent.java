package com.khmelenko.lab.varis.event.travis;

import com.khmelenko.lab.varis.network.response.Repo;

/**
 * Event on successful loading repository
 *
 * @author Dmytro Khmelenko
 */
public final class RepoLoadedEvent {

    private final Repo mRepo;

    public RepoLoadedEvent(Repo repo) {
        mRepo = repo;
    }

    public Repo getRepo() {
        return mRepo;
    }
}
