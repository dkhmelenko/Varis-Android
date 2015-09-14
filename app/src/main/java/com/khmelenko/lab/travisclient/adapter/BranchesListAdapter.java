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
import com.khmelenko.lab.travisclient.network.response.Branch;
import com.khmelenko.lab.travisclient.network.response.Branches;
import com.khmelenko.lab.travisclient.network.response.Commit;
import com.khmelenko.lab.travisclient.util.DateTimeUtils;

/**
 * List adapter for branches
 *
 * @author Dmytro Khmelenko
 */
public class BranchesListAdapter extends RecyclerView.Adapter<BranchesListAdapter.BranchViewHolder> {

    private Branches mBranches;
    private final Context mContext;

    public BranchesListAdapter(Context context, Branches branches) {
        mContext = context;
        mBranches = branches;
    }

    @Override
    public BranchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_branch, parent, false);
        return new BranchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BranchViewHolder holder, int position) {
        if (mBranches != null) {
            Branch branch = mBranches.getBranches().get(position);
            Commit relatedCommit = null;
            for (Commit commit : mBranches.getCommits()) {
                if (branch.getCommitId() == commit.getId()) {
                    relatedCommit = commit;
                    break;
                }
            }

            // build data
            holder.mNumber.setText(mContext.getString(R.string.build_build_number, branch.getNumber()));
            String state = branch.getState();
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
            if (!TextUtils.isEmpty(branch.getFinishedAt())) {
                String formattedDate = DateTimeUtils.parseAndFormatDateTime(branch.getFinishedAt());
                formattedDate = mContext.getString(R.string.build_finished_at, formattedDate);
                holder.mFinished.setText(formattedDate);
            } else {
                String stateInProgress = mContext.getString(R.string.build_build_in_progress);
                String finished = mContext.getString(R.string.build_finished_at, stateInProgress);
                holder.mFinished.setText(finished);
            }

            // duration
            if (branch.getDuration() != 0) {
                String duration = TimeConverter.durationToString(branch.getDuration());
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
        return mBranches != null ? mBranches.getBranches().size() : 0;
    }

    public void setBranches(Branches repoStatus) {
        mBranches = repoStatus;
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
            mNumber = (TextView) itemView.findViewById(R.id.item_branch_number);
            mState = (TextView) itemView.findViewById(R.id.item_branch_state);
            mBranch = (TextView) itemView.findViewById(R.id.item_branch_branch);
            mCommitMessage = (TextView) itemView.findViewById(R.id.item_branch_commit_message);
            mCommitPerson = (TextView) itemView.findViewById(R.id.item_branch_commit_person);
            mDuration = (TextView) itemView.findViewById(R.id.item_branch_duration);
            mFinished = (TextView) itemView.findViewById(R.id.item_branch_finished);
        }
    }
}
