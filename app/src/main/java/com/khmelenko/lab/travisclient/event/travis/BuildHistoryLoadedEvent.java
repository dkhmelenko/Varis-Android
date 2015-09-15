package com.khmelenko.lab.travisclient.event.travis;

import com.khmelenko.lab.travisclient.network.response.BuildHistory;

/**
 * Event on completed loading build history
 *
 * @author Dmytro Khmelenko
 */
public class BuildHistoryLoadedEvent {

    private final BuildHistory mBuildHistory;

    public BuildHistoryLoadedEvent(BuildHistory buildHistory) {
        mBuildHistory = buildHistory;
    }

    public BuildHistory getBuildHistory() {
        return mBuildHistory;
    }
}
