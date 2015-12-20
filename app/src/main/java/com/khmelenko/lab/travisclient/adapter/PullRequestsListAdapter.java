package com.khmelenko.lab.travisclient.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.network.response.Commit;
import com.khmelenko.lab.travisclient.network.response.RequestData;
import com.khmelenko.lab.travisclient.network.response.Requests;
import com.khmelenko.lab.travisclient.view.BuildView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * List adapter for Pull requests
 *
 * @author Dmytro Khmelenko
 */
public class PullRequestsListAdapter extends RecyclerView.Adapter<PullRequestsListAdapter.BranchViewHolder> {

    private Requests mRequests;
    private List<RequestData> mPullRequests;
    private final Context mContext;
    private final OnListItemListener mListener;

    public PullRequestsListAdapter(Context context, Requests requests, OnListItemListener listener) {
        mContext = context;
        mRequests = requests;
        mPullRequests = new ArrayList<>();
        mListener = listener;
    }

    /**
     * Sets requests
     *
     * @param requests Requests
     */
    public void setRequests(Requests requests, List<RequestData> pullRequests) {
        mRequests = requests;
        mPullRequests = pullRequests;
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

        holder.mBuildView.setPullRequestData(request, relatedBuild, relatedCommit);
    }

    @Override
    public int getItemCount() {
        return mPullRequests.size();
    }

    /**
     * Viewholder class
     */
    class BranchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.item_pull_request_card_view)
        View mParent;

        @Bind(R.id.item_pull_request_data)
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
