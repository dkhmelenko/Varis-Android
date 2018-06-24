package com.khmelenko.lab.varis.converter

import android.graphics.drawable.Drawable
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.content.ContextCompat
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.TravisApp

/**
 * Provides methods for processing build states
 *
 * @author Dmytro Khmelenko
 */
object BuildStateHelper {

    private const val STATE_CREATED = "created"
    private const val STATE_STARTED = "started"
    private const val STATE_PASSED = "passed"
    private const val STATE_CANCELED = "canceled"
    private const val STATE_FAILED = "failed"
    private const val STATE_ERRORED = "errored"

    /**
     * Gets the color according to the build state
     *
     * @param state Build state
     * @return Color
     */
    @JvmStatic
    fun getBuildColor(state: String): Int {
        val context = TravisApp.appContext!!

        var color = ContextCompat.getColor(context, R.color.secondary_text)
        when (state) {
            STATE_CREATED, STATE_STARTED -> color = ContextCompat.getColor(context, R.color.build_state_started)
            STATE_PASSED -> color = ContextCompat.getColor(context, R.color.build_state_passed)
            STATE_CANCELED, STATE_FAILED -> color = ContextCompat.getColor(context, R.color.build_state_failed)
            STATE_ERRORED -> color = ContextCompat.getColor(context, R.color.build_state_errored)
        }

        return color
    }

    /**
     * Gets the image drawable according to the build state
     *
     * @param state Build state
     * @return Image drawable
     */
    @JvmStatic
    fun getBuildImage(state: String): Drawable? {
        val context = TravisApp.appContext!!

        return when (state) {
            STATE_CREATED -> ContextCompat.getDrawable(context, R.drawable.ic_build_state_created_16dp)
            STATE_STARTED -> {
                val drawable = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_build_state_started_animated_16dp)
                drawable?.start()
                drawable
            }
            STATE_PASSED -> ContextCompat.getDrawable(context, R.drawable.ic_build_state_passed_16dp)
            STATE_CANCELED, STATE_ERRORED -> ContextCompat.getDrawable(context, R.drawable.ic_build_state_errored_16dp)
            STATE_FAILED -> ContextCompat.getDrawable(context, R.drawable.ic_build_state_failed_16dp)
            else -> null
        }
    }

    /**
     * Checks whether the state passed or not
     *
     * @param state State
     * @return True, if state is passed. False otherwise
     */
    @JvmStatic
    fun isPassed(state: String): Boolean {
        return state == STATE_PASSED
    }

    /**
     * Checks whether the build is in progress or not
     *
     * @param state State
     * @return True, if the build is in progress state. False otherwise
     */
    @JvmStatic
    fun isInProgress(state: String): Boolean {
        return state == STATE_CREATED || state == STATE_STARTED
    }
}
