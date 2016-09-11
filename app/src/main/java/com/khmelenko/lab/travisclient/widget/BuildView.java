package com.khmelenko.lab.travisclient.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.converter.BuildStateHelper;
import com.khmelenko.lab.travisclient.converter.TimeConverter;
import com.khmelenko.lab.travisclient.network.response.Branch;
import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.network.response.Commit;
import com.khmelenko.lab.travisclient.network.response.RequestData;
import com.khmelenko.lab.travisclient.util.DateTimeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * View with build info
 *
 * @author Dmytro Khmelenko
 */
public class BuildView extends LinearLayout {

    @Bind(R.id.build_number)
    TextView mNumber;

    @Bind(R.id.build_state)
    TextView mState;

    @Bind(R.id.build_pull_request_title)
    TextView mTitle;

    @Bind(R.id.build_branch)
    TextView mBranch;

    @Bind(R.id.build_commit_message)
    TextView mCommitMessage;

    @Bind(R.id.build_commit_person)
    TextView mCommitPerson;

    @Bind(R.id.build_duration)
    TextView mDuration;

    @Bind(R.id.build_finished)
    TextView mFinished;

    @Bind(R.id.build_pull_request_section)
    View mPullRequestSection;

    @Bind(R.id.build_build_commit_msg_section)
    View mCommitMessageSection;

    @Bind(R.id.build_branch_section)
    View mBuildBranchSection;

    public BuildView(Context context) {
        super(context);
        initializeViews(context);
    }

    public BuildView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public BuildView(Context context,
                     AttributeSet attrs,
                     int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_build, this);
        ButterKnife.bind(this, view);
    }

    /**
     * Shows pull request details
     *
     * @param isPullRequest True, if pull request details should be shown. False otherwise
     */
    private void showPullRequestDetails(boolean isPullRequest) {
        if (isPullRequest) {
            mPullRequestSection.setVisibility(VISIBLE);
            mBuildBranchSection.setVisibility(GONE);
            mCommitMessageSection.setVisibility(GONE);
        } else {
            mPullRequestSection.setVisibility(GONE);
            mBuildBranchSection.setVisibility(VISIBLE);
            mCommitMessageSection.setVisibility(VISIBLE);
        }
    }

    /**
     * Sets build state
     *
     * @param state Build state
     */
    private void setBuildState(String state) {
        if (!TextUtils.isEmpty(state)) {
            int buildColor = BuildStateHelper.getBuildColor(state);
            mState.setText(state);
            mState.setTextColor(buildColor);
            mNumber.setTextColor(buildColor);

            Drawable drawable = BuildStateHelper.getBuildImage(state);
            if (drawable != null) {
                drawable.setBounds(0, 0, 30, 30);
                mNumber.setCompoundDrawables(drawable, null, null, null);
            }
        }
    }

    /**
     * Sets build duration
     *
     * @param durationInMillis Duration
     */
    private void setBuildDuration(long durationInMillis) {
        if (durationInMillis != 0) {
            String duration = TimeConverter.durationToString(durationInMillis);
            duration = getContext().getString(R.string.build_duration, duration);
            mDuration.setText(duration);
        } else {
            String stateInProgress = getContext().getString(R.string.build_build_in_progress);
            String duration = getContext().getString(R.string.build_duration, stateInProgress);
            mDuration.setText(duration);
        }
    }

    /**
     * Sets the message when the build was finished
     *
     * @param finishedAt String with the message when the build was finished
     */
    private void setBuildFinishedAt(String finishedAt) {
        if (!TextUtils.isEmpty(finishedAt)) {
            String formattedDate = DateTimeUtils.parseAndFormatDateTime(finishedAt);
            formattedDate = getContext().getString(R.string.build_finished_at, formattedDate);
            mFinished.setText(formattedDate);
        } else {
            String stateInProgress = getContext().getString(R.string.build_build_in_progress);
            String finished = getContext().getString(R.string.build_finished_at, stateInProgress);
            mFinished.setText(finished);
        }
    }

    /**
     * Sets build data
     *
     * @param build  Build
     * @param commit Related commit
     */
    public void setBuildData(Build build, Commit commit) {
        showPullRequestDetails(false);

        // build data
        mNumber.setText(getContext().getString(R.string.build_build_number, build.getNumber()));
        setBuildState(build.getState());

        // commit data
        if (commit != null) {
            mBranch.setText(commit.getBranch());
            mCommitMessage.setText(commit.getMessage());
            mCommitPerson.setText(commit.getCommitterName());
        }

        // finished at
        setBuildFinishedAt(build.getFinishedAt());

        // duration
        setBuildDuration(build.getDuration());
    }

    /**
     * Sets branch data
     *
     * @param branch Branch
     * @param commit Last commit
     */
    public void setBranchData(Branch branch, Commit commit) {
        showPullRequestDetails(false);

        // build data
        mNumber.setText(getContext().getString(R.string.build_build_number, branch.getNumber()));
        setBuildState(branch.getState());

        // commit data
        if (commit != null) {
            mBranch.setText(commit.getBranch());
            mCommitMessage.setText(commit.getMessage());
            mCommitPerson.setText(commit.getCommitterName());
        }

        // finished at
        setBuildFinishedAt(branch.getFinishedAt());

        // duration
        setBuildDuration(branch.getDuration());
    }

    /**
     * Sets pull request data
     *
     * @param request Pull request data
     * @param build   Build
     * @param commit  Commit
     */
    public void setPullRequestData(RequestData request, Build build, Commit commit) {
        showPullRequestDetails(true);

        mNumber.setText(getContext().getString(R.string.pull_request_number, request.getPullRequestNumber()));
        mTitle.setText(request.getPullRequestTitle());

        // commit data
        if (commit != null) {
            mCommitPerson.setText(commit.getCommitterName());
        }

        // build data
        if (build != null) {
            setBuildState(build.getState());

            // finished at
            setBuildFinishedAt(build.getFinishedAt());

            // duration
            setBuildDuration(build.getDuration());
        }
    }
}