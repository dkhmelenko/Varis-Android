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

    @Bind(R.id.build_indicator)
    View mIndicator;

    @Bind(R.id.build_number)
    TextView mNumber;

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
            mTitle.setVisibility(VISIBLE);
            mBranch.setVisibility(GONE);
            mCommitMessage.setVisibility(GONE);
        } else {
            mTitle.setVisibility(GONE);
            mBranch.setVisibility(VISIBLE);
            mCommitMessage.setVisibility(VISIBLE);
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
            mDuration.setVisibility(VISIBLE);
        } else {
            mDuration.setVisibility(GONE);
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
            mFinished.setVisibility(VISIBLE);
        } else {
            mFinished.setVisibility(GONE);
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
        setBuildNumberState(build.getState(), build.getNumber());

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
        setBuildNumberState(branch.getState(), branch.getNumber());

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

    private void setBuildNumberState(String state, String number) {
        mNumber.setText(getContext().getString(R.string.build_build_number, number,
                state));
        if (!TextUtils.isEmpty(state)) {
            int buildColor = BuildStateHelper.getBuildColor(state);
            mIndicator.setBackgroundColor(buildColor);
            mNumber.setTextColor(buildColor);

            Drawable drawable = BuildStateHelper.getBuildImage(state);
            if (drawable != null) {
                mNumber.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        }
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
            setBuildNumberState(build.getNumber(), build.getState());

            // finished at
            setBuildFinishedAt(build.getFinishedAt());

            // duration
            setBuildDuration(build.getDuration());
        }
    }
}