package com.khmelenko.lab.travisclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.adapter.viewholder.BuildViewHolder;
import com.khmelenko.lab.travisclient.network.response.Branch;
import com.khmelenko.lab.travisclient.network.response.Branches;
import com.khmelenko.lab.travisclient.network.response.Commit;

/**
 * List adapter for branches
 *
 * @author Dmytro Khmelenko
 */
public final class BranchesListAdapter extends RecyclerView.Adapter<BuildViewHolder> {

    private Branches mBranches;
    private final OnListItemListener mListener;

    public BranchesListAdapter(Branches branches, OnListItemListener listener) {
        mBranches = branches;
        mListener = listener;
    }

    @Override
    public BuildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_build_view, parent, false);
        return new BuildViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(BuildViewHolder holder, int position) {
        if (mBranches != null) {
            Branch branch = mBranches.getBranches().get(position);
            Commit relatedCommit = null;
            for (Commit commit : mBranches.getCommits()) {
                if (branch.getCommitId() == commit.getId()) {
                    relatedCommit = commit;
                    break;
                }
            }

            holder.mBuildView.setState(branch);
            holder.mBuildView.setCommit(relatedCommit);
        }
    }

    @Override
    public int getItemCount() {
        return mBranches != null ? mBranches.getBranches().size() : 0;
    }

    public void setBranches(Branches repoStatus) {
        mBranches = repoStatus;
    }

}
