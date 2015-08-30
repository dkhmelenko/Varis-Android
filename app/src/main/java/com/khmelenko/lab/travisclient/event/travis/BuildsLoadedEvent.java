package com.khmelenko.lab.travisclient.event.travis;

import com.khmelenko.lab.travisclient.network.response.Build;

import java.util.List;

/**
 * Event on completed loading builds
 *
 * @author Dmytro Khmelenko
 */
public class BuildsLoadedEvent {

    private final List<Build> mBuilds;

    public BuildsLoadedEvent(List<Build> builds) {
        mBuilds = builds;
    }

    public List<Build> getBuilds() {
        return mBuilds;
    }
}
