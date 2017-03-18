package com.khmelenko.lab.varis.event.travis;

import com.khmelenko.lab.varis.network.response.BuildHistory;

/**
 * Event on completed loading build history
 *
 * @author Dmytro Khmelenko
 */
public final class BuildHistoryLoadedEvent {

    private final BuildHistory mBuildHistory;

    public BuildHistoryLoadedEvent(BuildHistory buildHistory) {
        mBuildHistory = buildHistory;
    }

    public BuildHistory getBuildHistory() {
        return mBuildHistory;
    }
}
