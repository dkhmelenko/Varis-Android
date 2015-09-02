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
import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.network.response.Commit;
import com.khmelenko.lab.travisclient.network.response.RepoStatus;
import com.khmelenko.lab.travisclient.converter.TimeConverter;
import com.khmelenko.lab.travisclient.util.DateTimeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter for the list of builds
 *
 * @author Dmytro Khmelenko
 */
public class BuildListAdapter extends RecyclerView.Adapter<BuildListAdapter.BuildViewHolder> {

    private final RepoStatus mRepoStatus;
    private final Context mContext;

    public BuildListAdapter(Context context, RepoStatus repoStatus) {
        mContext = context;
        mRepoStatus = repoStatus;
    }

    @Override
    public BuildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repo, parent, false);
        return new BuildViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BuildViewHolder holder, int position) {
        Build build = mRepoStatus.getBuilds().get(position);
        Commit relatedCommit = null;
        for(Commit commit : mRepoStatus.getCommits()) {
            if(build.getCommitId() == commit.getId()) {
                relatedCommit = commit;
                break;
            }
        }

        // build data
        holder.mNumber.setText(mContext.getString(R.string.build_build_number, build.getNumber()));
        String state = build.getState();
        if (!TextUtils.isEmpty(state)) {
            int buildColor = BuildStateHelper.getBuildColor(state);
            holder.mState.setText(state);
            holder.mState.setTextColor(buildColor);

            Drawable drawable = BuildStateHelper.getBuildImage(state);
            drawable.setBounds(0,0,30,30);
            holder.mNumber.setCompoundDrawables(drawable, null, null, null);
        }

        // commit data
        if(relatedCommit != null) {
            holder.mBranch.setText(relatedCommit.getBranch());
            holder.mCommitMessage.setText(relatedCommit.getMessage());
            holder.mCommitPerson.setText(relatedCommit.getCommitterName());
        }

        // finished at
        if (!TextUtils.isEmpty(build.getFinishedAt())) {
            String formattedDate = DateTimeUtils.parseAndFormatDateTime(build.getFinishedAt());
            formattedDate = mContext.getString(R.string.build_finished_at, formattedDate);
            holder.mFinished.setText(formattedDate);
        } else {
            String stateInProgress = mContext.getString(R.string.build_build_in_progress);
            String finished = mContext.getString(R.string.build_finished_at, stateInProgress);
            holder.mFinished.setText(finished);
        }

        // duration
        if(build.getDuration() != 0) {
            String duration = TimeConverter.durationToString(build.getDuration());
            duration = mContext.getString(R.string.build_duration, duration);
            holder.mDuration.setText(duration);
        } else {
            String stateInProgress = mContext.getString(R.string.build_build_in_progress);
            String duration = mContext.getString(R.string.build_duration, stateInProgress);
            holder.mDuration.setText(duration);
        }

    }

    @Override
    public int getItemCount() {
        return mRepoStatus.getBuilds().size();
    }

    /**
     * Viewholder class
     */
    class BuildViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_build_number)
        TextView mNumber;
        @Bind(R.id.item_build_state)
        TextView mState;
        @Bind(R.id.item_build_branch)
        TextView mBranch;
        @Bind(R.id.item_build_commit_message)
        TextView mCommitMessage;
        @Bind(R.id.item_build_commit_person)
        TextView mCommitPerson;
        @Bind(R.id.item_build_duration)
        TextView mDuration;
        @Bind(R.id.item_build_finished)
        TextView mFinished;

        public BuildViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
        }
    }
}
