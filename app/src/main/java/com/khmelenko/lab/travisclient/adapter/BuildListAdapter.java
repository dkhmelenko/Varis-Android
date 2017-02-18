package com.khmelenko.lab.travisclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.adapter.viewholder.BuildViewHolder;
import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.network.response.BuildHistory;
import com.khmelenko.lab.travisclient.network.response.Commit;

/**
 * Adapter for the list of builds
 *
 * @author Dmytro Khmelenko
 */
public final class BuildListAdapter extends RecyclerView.Adapter<BuildViewHolder> {

    private BuildHistory mBuildHistory;
    private final OnListItemListener mListener;

    public BuildListAdapter(BuildHistory buildHistory, OnListItemListener listener) {
        mBuildHistory = buildHistory;
        mListener = listener;
    }

    @Override
    public BuildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_build_view, parent, false);
        return new BuildViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(BuildViewHolder holder, int position) {
        if (mBuildHistory != null) {
            Build build = mBuildHistory.getBuilds().get(position);
            Commit relatedCommit = null;
            for (Commit commit : mBuildHistory.getCommits()) {
                if (build.getCommitId() == commit.getId()) {
                    relatedCommit = commit;
                    break;
                }
            }
            holder.mBuildView.setState(build);
            holder.mBuildView.setCommit(relatedCommit);
        }
    }

    @Override
    public int getItemCount() {
        return mBuildHistory != null ? mBuildHistory.getBuilds().size() : 0;
    }

    /**
     * Sets build history
     *
     * @param buildHistory Build history
     */
    public void setBuildHistory(BuildHistory buildHistory) {
        mBuildHistory = buildHistory;
    }

}
