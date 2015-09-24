package com.khmelenko.lab.travisclient.event.travis;

import com.khmelenko.lab.travisclient.network.response.BuildDetails;

/**
 * Event on completed loading build details
 *
 * @author Dmytro Khmelenko
 */
public final class BuildDetailsLoadedEvent {

    private final BuildDetails mBuildDetails;


    public BuildDetailsLoadedEvent(BuildDetails buildDetails) {
        mBuildDetails = buildDetails;
    }

    public BuildDetails getBuildDetails() {
        return mBuildDetails;
    }
}
