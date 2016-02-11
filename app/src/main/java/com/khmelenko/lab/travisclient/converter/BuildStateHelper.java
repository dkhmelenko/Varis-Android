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
            case "created":
            case "started":
                color = ContextCompat.getColor(context, R.color.build_state_started);
                break;
            case "passed":
                color = ContextCompat.getColor(context, R.color.build_state_passed);
                break;
            case "canceled":
            case "failed":
            case "errored":
                color = ContextCompat.getColor(context, R.color.build_state_failed);
                break;
        }

        return color;
    }

    public static int getBuildBackground(String state) {
        Context context = TravisApp.getAppContext();

        int color = ContextCompat.getColor(context, android.R.color.transparent);
        switch (state) {
            case "created":
            case "started":
                color = ContextCompat.getColor(context, R.color.build_state_started_bg);
                break;
            case "passed":
                color = ContextCompat.getColor(context, R.color.build_state_passed_bg);
                break;
            case "canceled":
            case "failed":
            case "errored":
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
    public static
    @Nullable
    Drawable getBuildImage(String state) {
        Context context = TravisApp.getAppContext();

        Drawable drawable = null;
        switch (state) {
            case "created":
            case "started":
                drawable = ContextCompat.getDrawable(context, R.drawable.build_state_started);
                break;
            case "passed":
                drawable = ContextCompat.getDrawable(context, R.drawable.build_state_passed);
                break;
            case "canceled":
            case "errored":
                drawable = ContextCompat.getDrawable(context, R.drawable.build_state_errored);
                break;
            case "failed":
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
        boolean passed = state.equals("passed");
        return passed;
    }

    /**
     * Checks whether the build is in progress or not
     *
     * @param state State
     * @return True, if the build is in progress state. False otherwise
     */
    public static boolean isInProgress(@NonNull String state) {
        boolean inProgress = state.equals("created") || state.equals("started");
        return inProgress;
    }
}
