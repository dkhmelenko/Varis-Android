package com.khmelenko.lab.travisclient.common;

/**
 * Application constant values
 *
 * @author Dmytro Khmelenko
 */
public final class Constants {

    // denied constructor
    private Constants() {
    }

    /**
     * URL for open source projects
     */
    public static final String OPEN_SOURCE_TRAVIS_URL = "https://api.travis-ci.org";

    /**
     * URL for private projects
     */
    public static final String PRIVATE_TRAVIS_URL = "https://api.travis-ci.com";
}
