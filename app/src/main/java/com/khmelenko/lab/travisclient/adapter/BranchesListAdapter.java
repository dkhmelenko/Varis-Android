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
import com.khmelenko.lab.travisclient.view.BuildView;

/**
 * List adapter for branches
 *
 * @author Dmytro Khmelenko
 */
public class BranchesListAdapter extends RecyclerView.Adapter<BranchesListAdapter.BranchViewHolder> {

    private Branches mBranches;
    private final Context mContext;
    private final OnListItemListener mListener;

    public BranchesListAdapter(Context context, Branches branches, OnListItemListener listener) {
        mContext = context;
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

            holder.mBuildView.setBranchData(branch, relatedCommit);
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

        View mParent;
        BuildView mBuildView;

        public BranchViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mParent = itemView.findViewById(R.id.card_view);
            mParent.setOnClickListener(this);
            mBuildView = (BuildView) itemView.findViewById(R.id.item_branch_data);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemSelected(getLayoutPosition());
            }
        }
    }
}
