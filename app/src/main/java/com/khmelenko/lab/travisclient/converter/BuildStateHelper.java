package com.khmelenko.lab.travisclient.converter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
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
                color = ContextCompat.getColor(context, R.color.build_state_failed);
                break;
            case STATE_ERRORED:
                color = ContextCompat.getColor(context, R.color.build_state_errored);
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
                drawable = AnimatedVectorDrawableCompat
                        .create(context, R.drawable.build_state_started_anim_16dp);
                break;
            case STATE_STARTED:
                AnimatedVectorDrawableCompat anDrawable = AnimatedVectorDrawableCompat
                        .create(context, R.drawable.build_state_started_anim_16dp);
                anDrawable.start();
                drawable = anDrawable;
                break;
            case STATE_PASSED:
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_build_state_passed_16dp);
                break;
            case STATE_CANCELED:
            case STATE_ERRORED:
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_build_state_errored_16dp);
                break;
            case STATE_FAILED:
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_build_state_failed_16dp);
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
        return state.equals(STATE_PASSED);
    }

    /**
     * Checks whether the build is in progress or not
     *
     * @param state State
     * @return True, if the build is in progress state. False otherwise
     */
    public static boolean isInProgress(@NonNull String state) {
        return state.equals(STATE_CREATED) || state.equals(STATE_STARTED);
    }
}
