package com.khmelenko.lab.travisclient.event.travis;

import com.khmelenko.lab.travisclient.network.response.Branch;

import java.util.List;

/**
 * Event on completed loading branches
 *
 * @author Dmytro Khmelenko
 */
public class BranchesLoadedEvent {


    private final List<Branch> mBranches;

    public BranchesLoadedEvent(List<Branch> branches) {
        mBranches = branches;
    }

    public List<Branch> getBranches() {
        return mBranches;
    }
}
