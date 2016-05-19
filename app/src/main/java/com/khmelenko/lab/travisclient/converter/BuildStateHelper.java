package com.khmelenko.lab.travisclient.converter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.TravisApp;

/**
 * Provides methods for processing build states
 *
 * @author Dmytro Khmelenko
 */
public final class BuildStateHelper {

    private static final String STATE_CREATED = "created";
    private static final String STATE_STARTED = "started";
    private static final String STATE_PASSED = "passed";
    private static final String STATE_CANCELED = "canceled";
    private static final String STATE_FAILED = "failed";
    private static final String STATE_ERRORED = "errored";

    /**
     * Gets the color according to the build state
     *
     * @param state Build state
     * @return Color
     */
    public static int getBuildColor(String state) {
        Context context = TravisApp.getAppContext();

        int color = ContextCompat.getColor(context, R.color.secondary_text);
        switch (state) {
            case STATE_CREATED:
            case STATE_STARTED:
                color = ContextCompat.getColor(context, R.color.build_state_started);
                break;
            case STATE_PASSED:
                color = ContextCompat.getColor(context, R.color.build_state_passed);
                break;
            case STATE_CANCELED:
            case STATE_FAILED:
            case STATE_ERRORED:
                color = ContextCompat.getColor(context, R.color.build_state_failed);
                break;
        }

        return color;
    }

    /**
     * Gets the color for the build background
     *
     * @param state Build state
     * @return Color
     */
    public static int getBuildBackground(String state) {
        Context context = TravisApp.getAppContext();

        int color = ContextCompat.getColor(context, android.R.color.transparent);
        switch (state) {
            case STATE_CREATED:
            case STATE_STARTED:
                color = ContextCompat.getColor(context, R.color.build_state_started_bg);
                break;
            case STATE_PASSED:
                color = ContextCompat.getColor(context, R.color.build_state_passed_bg);
                break;
            case STATE_CANCELED:
            case STATE_FAILED:
            case STATE_ERRORED:
                color = ContextCompat.getColor(context, R.color.build_state_failed_bg);
                break;
        }

        return color;
    }

    /**
     * Gets the image drawable according to the build state
     *
     * @param state Build state
     * @return Image drawable
     */
    @Nullable
    public static Drawable getBuildImage(String state) {
        Context context = TravisApp.getAppContext();

        Drawable drawable = null;
        switch (state) {
            case STATE_CREATED:
            case STATE_STARTED:
                drawable = ContextCompat.getDrawable(context, R.drawable.build_state_started);
                break;
            case STATE_PASSED:
                drawable = ContextCompat.getDrawable(context, R.drawable.build_state_passed);
                break;
            case STATE_CANCELED:
            case STATE_ERRORED:
                drawable = ContextCompat.getDrawable(context, R.drawable.build_state_errored);
                break;
            case STATE_FAILED:
                drawable = ContextCompat.getDrawable(context, R.drawable.build_state_failed);
                break;
        }

        return drawable;
    }

    /**
     * Checks whether the state passed or not
     *
     * @param state State
     * @return True, if state is passed. False otherwise
     */
    public static boolean isPassed(@NonNull String state) {
        boolean passed = state.equals(STATE_PASSED);
        return passed;
    }

    /**
     * Checks whether the build is in progress or not
     *
     * @param state State
     * @return True, if the build is in progress state. False otherwise
     */
    public static boolean isInProgress(@NonNull String state) {
        boolean inProgress = state.equals(STATE_CREATED) || state.equals(STATE_STARTED);
        return inProgress;
    }
}
