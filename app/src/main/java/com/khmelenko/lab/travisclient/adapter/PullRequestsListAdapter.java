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
import com.khmelenko.lab.travisclient.network.response.Commit;
import com.khmelenko.lab.travisclient.network.response.RequestData;
import com.khmelenko.lab.travisclient.network.response.Requests;
import com.khmelenko.lab.travisclient.util.DateTimeUtils;

/**
 * List adapter for Pull requests
 *
 * @author Dmytro Khmelenko
 */
public class PullRequestsListAdapter extends RecyclerView.Adapter<PullRequestsListAdapter.BranchViewHolder> {

    private Requests mRequests;
    private final Context mContext;

    public PullRequestsListAdapter(Context context, Requests requests) {
        mContext = context;
        mRequests = requests;
    }

    /**
     * Sets requests
     *
     * @param requests Requests
     */
    public void setRequests(Requests requests) {
        mRequests = requests;
    }

    @Override
    public BranchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pull_request, parent, false);
        return new BranchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BranchViewHolder holder, int position) {
        if (mRequests != null) {
            RequestData request = mRequests.getRequests().get(position);
            Commit relatedCommit = null;
            for (Commit commit : mRequests.getCommits()) {
                if (request.getCommitId() == commit.getId()) {
                    relatedCommit = commit;
                    break;
                }
            }

            // build data
            holder.mNumber.setText(mContext.getString(R.string.build_build_number, request.getNumber()));
            String state = request.getState();
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

            // commit data
            if (relatedCommit != null) {
                holder.mBranch.setText(relatedCommit.getBranch());
                holder.mCommitMessage.setText(relatedCommit.getMessage());
                holder.mCommitPerson.setText(relatedCommit.getCommitterName());
            }

            // finished at
            if (!TextUtils.isEmpty(request.getFinishedAt())) {
                String formattedDate = DateTimeUtils.parseAndFormatDateTime(request.getFinishedAt());
                formattedDate = mContext.getString(R.string.build_finished_at, formattedDate);
                holder.mFinished.setText(formattedDate);
            } else {
                String stateInProgress = mContext.getString(R.string.build_build_in_progress);
                String finished = mContext.getString(R.string.build_finished_at, stateInProgress);
                holder.mFinished.setText(finished);
            }

            // duration
            if (request.getDuration() != 0) {
                String duration = TimeConverter.durationToString(request.getDuration());
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
        return mRequests != null ? mRequests.getBranches().size() : 0;
    }

    /**
     * Viewholder class
     */
    class BranchViewHolder extends RecyclerView.ViewHolder {

        View mParent;
        TextView mNumber;
        TextView mState;
        TextView mBranch;
        TextView mCommitMessage;
        TextView mCommitPerson;
        TextView mDuration;
        TextView mFinished;

        public BranchViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mParent = itemView.findViewById(R.id.card_view);
            mNumber = (TextView) itemView.findViewById(R.id.item_pull_request_number);
            mState = (TextView) itemView.findViewById(R.id.item_pull_request_state);
            mBranch = (TextView) itemView.findViewById(R.id.item_pull_request_branch);
            mCommitMessage = (TextView) itemView.findViewById(R.id.item_pull_request_commit_message);
            mCommitPerson = (TextView) itemView.findViewById(R.id.item_pull_request_commit_person);
            mDuration = (TextView) itemView.findViewById(R.id.item_pull_request_duration);
            mFinished = (TextView) itemView.findViewById(R.id.item_pull_request_finished);
        }
    }
}
