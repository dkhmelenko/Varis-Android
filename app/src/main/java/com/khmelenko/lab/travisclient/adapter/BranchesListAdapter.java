package com.khmelenko.lab.travisclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.network.response.Branch;
import com.khmelenko.lab.travisclient.network.response.Branches;
import com.khmelenko.lab.travisclient.network.response.Commit;
import com.khmelenko.lab.travisclient.widget.BuildView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * List adapter for branches
 *
 * @author Dmytro Khmelenko
 */
public final class BranchesListAdapter extends RecyclerView.Adapter<BranchesListAdapter.BranchViewHolder> {

    private Branches mBranches;
    private final OnListItemListener mListener;

    public BranchesListAdapter(Branches branches, OnListItemListener listener) {
        mBranches = branches;
        mListener = listener;
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

            holder.mBuildView.setBuildState(branch);
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

    /**
     * Viewholder class
     */
    class BranchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.card_view)
        View mParent;

        @Bind(R.id.item_branch_data)
        BuildView mBuildView;

        public BranchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
            mParent.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemSelected(getLayoutPosition());
            }
        }
    }
}
