package com.khmelenko.lab.travisclient.event.travis;

import com.khmelenko.lab.travisclient.network.response.Branch;
import com.khmelenko.lab.travisclient.network.response.Branches;

import java.util.List;

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
