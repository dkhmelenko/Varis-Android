package com.khmelenko.lab.varis.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.converter.BuildStateHelper
import com.khmelenko.lab.varis.converter.TimeConverter
import com.khmelenko.lab.varis.network.response.Commit
import com.khmelenko.lab.varis.network.response.IBuildState
import com.khmelenko.lab.varis.network.response.RequestData
import com.khmelenko.lab.varis.util.DateTimeUtils
import kotlinx.android.synthetic.main.view_build.view.build_branch
import kotlinx.android.synthetic.main.view_build.view.build_commit_message
import kotlinx.android.synthetic.main.view_build.view.build_commit_person
import kotlinx.android.synthetic.main.view_build.view.build_duration
import kotlinx.android.synthetic.main.view_build.view.build_finished
import kotlinx.android.synthetic.main.view_build.view.build_indicator
import kotlinx.android.synthetic.main.view_build.view.build_number
import kotlinx.android.synthetic.main.view_build.view.build_pull_request_title

/**
 * View with build info
 *
 * @author Dmytro Khmelenko
 */
class BuildView : LinearLayout {

    constructor(context: Context) : super(context) {
        initializeViews(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initializeViews(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initializeViews(context)
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context the current context for the view.
     */
    private fun initializeViews(context: Context) {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_build, this)
    }

    /**
     * Sets build duration
     *
     * @param durationInMillis Duration
     */
    fun setDuration(durationInMillis: Long) {
        if (durationInMillis != 0L) {
            var duration = TimeConverter.durationToString(durationInMillis)
            duration = context.getString(R.string.build_duration, duration)
            build_duration.text = duration
            build_duration.visibility = View.VISIBLE
        } else {
            build_duration.visibility = View.GONE
        }
    }

    /**
     * Sets the message when the build was finished
     *
     * @param finishedAt String with the message when the build was finished
     */
    fun setFinishedAt(finishedAt: String) {
        if (!TextUtils.isEmpty(finishedAt)) {
            var formattedDate = DateTimeUtils.parseAndFormatDateTime(finishedAt)
            formattedDate = context.getString(R.string.build_finished_at, formattedDate)
            build_finished.text = formattedDate
            build_finished.visibility = View.VISIBLE
        } else {
            build_finished.visibility = View.GONE
        }
    }

    /**
     * Sets build state
     *
     * @param buildState Build state
     */
    fun setState(buildState: IBuildState?) {
        if (buildState != null) {
            setTitle(context
                    .getString(R.string.build_build_number, buildState.number, buildState.state))
            setStateIndicator(buildState.state)
            setFinishedAt(buildState.finishedAt)
            setDuration(buildState.duration)
        }
    }

    /**
     * Sets commit data
     *
     * @param commit Last commit
     */
    fun setCommit(commit: Commit?) {
        if (commit != null) {
            build_branch.visibility = View.VISIBLE
            build_branch.text = commit.branch
            build_commit_message.visibility = View.VISIBLE
            build_commit_message.text = commit.message
            build_commit_person.visibility = View.VISIBLE
            build_commit_person.text = commit.committerName
        }
    }

    /**
     * Sets title
     *
     * @param title Title
     */
    fun setTitle(title: String) {
        build_number.text = title
    }

    /**
     * Sets state indicator
     *
     * @param state Build state
     */
    fun setStateIndicator(state: String) {
        if (!TextUtils.isEmpty(state)) {
            val buildColor = BuildStateHelper.getBuildColor(state)
            build_indicator.setBackgroundColor(buildColor)
            build_number.setTextColor(buildColor)

            val drawable = BuildStateHelper.getBuildImage(state)
            if (drawable != null) {
                build_number.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            }
        }
    }

    /**
     * Sets the title for pull request
     *
     * @param title Title for Pull Request
     */
    fun setPullRequestTitle(title: RequestData) {
        build_pull_request_title.visibility = View.VISIBLE
        build_pull_request_title.text = title.pullRequestTitle
    }
}
