package com.khmelenko.lab.travisclient.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.converter.BuildStateHelper;
import com.khmelenko.lab.travisclient.converter.TimeConverter;
import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.network.response.Commit;
import com.khmelenko.lab.travisclient.network.response.RequestData;
import com.khmelenko.lab.travisclient.network.response.Requests;
import com.khmelenko.lab.travisclient.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * List adapter for Pull requests
 *
 * @author Dmytro Khmelenko
 */
public class PullRequestsListAdapter extends RecyclerView.Adapter<PullRequestsListAdapter.BranchViewHolder> {

    private Requests mRequests;
    private List<RequestData> mPullRequests;
    private final Context mContext;

    public PullRequestsListAdapter(Context context, Requests requests) {
        mContext = context;
        mRequests = requests;
        mPullRequests = new ArrayList<>();
        fetchPullRequests();
    }

    /**
     * Sets requests
     *
     * @param requests Requests
     */
    public void setRequests(Requests requests) {
        mRequests = requests;
        fetchPullRequests();
    }

    /**
     * Fetches pull requests
     */
    private void fetchPullRequests() {
        if(mRequests != null) {
            for (RequestData request : mRequests.getRequests()) {
                if (request.isPullRequest() && !mPullRequests.contains(request)) {
                    mPullRequests.add(request);
                }
            }
        }
    }

    @Override
    public BranchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pull_request, parent, false);
        return new BranchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BranchViewHolder holder, int position) {
        if (mRequests != null) {
            RequestData request = mPullRequests.get(position);
            bindPullRequest(holder, position, request);
        }
    }

    private void bindPullRequest(BranchViewHolder holder, int position, RequestData request) {
        Commit relatedCommit = null;
        for (Commit commit : mRequests.getCommits()) {
            if (request.getCommitId() == commit.getId()) {
                relatedCommit = commit;
                break;
            }
        }

        Build relatedBuild = null;
        for (Build build : mRequests.getBuilds()) {
            if (request.getBuildId() == build.getId()) {
                relatedBuild = build;
                break;
            }
        }

        holder.mNumber.setText(mContext.getString(R.string.pull_request_number, request.getPullRequestNumber()));
        holder.mTitle.setText(request.getPullRequestTitle());

        // commit data
        if (relatedCommit != null) {
            holder.mCommitPerson.setText(relatedCommit.getCommitterName());
        }

        // build data
        if (relatedBuild != null) {
            String state = relatedBuild.getState();
            if (!TextUtils.isEmpty(state)) {
                int buildColor = BuildStateHelper.getBuildColor(state);
                holder.mState.setText(state);
                holder.mState.setTextColor(buildColor);
                holder.mNumber.setTextColor(buildColor);

                Drawable drawable = BuildStateHelper.getBuildImage(state);
                if (drawable != null) {
                    drawable.setBounds(0, 0, 30, 30);
                    holder.mNumber.setCompoundDrawables(drawable, null, null, null);
                }
            }

            // finished at
            if (!TextUtils.isEmpty(relatedBuild.getFinishedAt())) {
                String formattedDate = DateTimeUtils.parseAndFormatDateTime(relatedBuild.getFinishedAt());
                formattedDate = mContext.getString(R.string.build_finished_at, formattedDate);
                holder.mFinished.setText(formattedDate);
            } else {
                String stateInProgress = mContext.getString(R.string.build_build_in_progress);
                String finished = mContext.getString(R.string.build_finished_at, stateInProgress);
                holder.mFinished.setText(finished);
            }

            // duration
            if (relatedBuild.getDuration() != 0) {
                String duration = TimeConverter.durationToString(relatedBuild.getDuration());
                duration = mContext.getString(R.string.build_duration, duration);
                holder.mDuration.setText(duration);
            } else {
                String stateInProgress = mContext.getString(R.string.build_build_in_progress);
                String duration = mContext.getString(R.string.build_duration, stateInProgress);
                holder.mDuration.setText(duration);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPullRequests.size();
    }

    /**
     * Viewholder class
     */
    class BranchViewHolder extends RecyclerView.ViewHolder {

        View mParent;
        TextView mNumber;
        TextView mState;
        TextView mTitle;
        TextView mCommitPerson;
        TextView mDuration;
        TextView mFinished;

        public BranchViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mParent = itemView.findViewById(R.id.card_view);
            mNumber = (TextView) itemView.findViewById(R.id.item_pull_request_number);
            mState = (TextView) itemView.findViewById(R.id.item_pull_request_state);
            mTitle = (TextView) itemView.findViewById(R.id.item_pull_request_title);
            mCommitPerson = (TextView) itemView.findViewById(R.id.item_pull_request_commit_person);
            mDuration = (TextView) itemView.findViewById(R.id.item_pull_request_duration);
            mFinished = (TextView) itemView.findViewById(R.id.item_pull_request_finished);
        }
    }
}
