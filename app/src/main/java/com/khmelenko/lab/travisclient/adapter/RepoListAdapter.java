package com.khmelenko.lab.travisclient.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.converter.BuildStateHelper;
import com.khmelenko.lab.travisclient.converter.TimeConverter;
import com.khmelenko.lab.travisclient.network.response.Repo;
import com.khmelenko.lab.travisclient.util.DateTimeUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter class for the list of repositories
 *
 * @author Dmytro Khmelenko
 */
public final class RepoListAdapter extends RecyclerView.Adapter<RepoListAdapter.RepoViewHolder> {

    private final List<Repo> mRepos;
    private final Context mContext;
    private final OnListItemListener mListener;

    public RepoListAdapter(Context context, List<Repo> repos, OnListItemListener listener) {
        mContext = context;
        mRepos = repos;
        mListener = listener;
    }

    @Override
    public RepoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_repo, viewGroup, false);
        return new RepoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RepoViewHolder repoViewHolder, int i) {
        Repo repo = mRepos.get(i);
        repoViewHolder.mName.setText(repo.getSlug());
        String state = repo.getLastBuildState();
        if (!TextUtils.isEmpty(state)) {
            int buildColor = BuildStateHelper.getBuildColor(state);
            repoViewHolder.mName.setTextColor(buildColor);

            Drawable drawable = BuildStateHelper.getBuildImage(state);
            drawable.setBounds(0, 0, 30, 30);
            repoViewHolder.mName.setCompoundDrawables(drawable, null, null, null);
        }

        // finished at
        if (!TextUtils.isEmpty(repo.getLastBuildFinishedAt())) {
            String formattedDate = DateTimeUtils.parseAndFormatDateTime(repo.getLastBuildFinishedAt());
            formattedDate = mContext.getString(R.string.build_finished_at, formattedDate);
            repoViewHolder.mFinished.setText(formattedDate);
        } else {
            String stateInProgress = mContext.getString(R.string.build_build_in_progress);
            String finished = mContext.getString(R.string.build_finished_at, stateInProgress);
            repoViewHolder.mFinished.setText(finished);
        }

        // duration
        if (repo.getLastBuildDuration() != 0) {
            String duration = TimeConverter.durationToString(repo.getLastBuildDuration());
            duration = mContext.getString(R.string.repo_duration, duration);
            repoViewHolder.mDuration.setText(duration);
        } else {
            String stateInProgress = mContext.getString(R.string.repo_build_in_progress);
            String duration = mContext.getString(R.string.repo_duration, stateInProgress);
            repoViewHolder.mDuration.setText(duration);
        }
    }

    @Override
    public int getItemCount() {
        return mRepos.size();
    }

    /**
     * Viewholder class
     */
    class RepoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.item_repo_name)
        TextView mName;

        @Bind(R.id.item_repo_duration)
        TextView mDuration;

        @Bind(R.id.item_repo_finished)
        TextView mFinished;

        public RepoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            CardView cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemSelected(getLayoutPosition());
            }
        }
    }

}
