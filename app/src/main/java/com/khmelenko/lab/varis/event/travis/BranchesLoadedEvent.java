package com.khmelenko.lab.varis.event.travis;

import com.khmelenko.lab.varis.network.response.Branches;

/**
 * Event on completed loading branches
 *
 * @author Dmytro Khmelenko
 */
public final class BranchesLoadedEvent {

    private final Branches mBranches;

    public BranchesLoadedEvent(Branches branches) {
        mBranches = branches;
    }

    public Branches getBranches() {
        return mBranches;
    }
}
