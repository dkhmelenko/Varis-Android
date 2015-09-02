package com.khmelenko.lab.travisclient.converter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

        int color = ContextCompat.getColor(context, R.color.build_state_failed);
        switch (state) {
            case "started":
                color = ContextCompat.getColor(context, R.color.build_state_started);
                break;
            case "passed":
                color = ContextCompat.getColor(context, R.color.build_state_passed);
                break;
            case "failed":
            case "errored":
                color = ContextCompat.getColor(context, R.color.build_state_failed);
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
    public static Drawable getBuildImage(String state) {
        Context context = TravisApp.getAppContext();

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.build_failed);
        switch (state) {
            case "started":
                // TODO drawable = ContextCompat.getDrawable(context, R.drawable.build_started);
                break;
            case "passed":
                drawable = ContextCompat.getDrawable(context, R.drawable.build_passed);
                break;
            case "errored":
                // TODO Use another image for errored state
            case "failed":
                drawable = ContextCompat.getDrawable(context, R.drawable.build_failed);
                break;
        }

        return drawable;
    }
}
