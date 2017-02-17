package com.khmelenko.lab.travisclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.adapter.viewholder.BuildViewHolder;
import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.network.response.Commit;
import com.khmelenko.lab.travisclient.network.response.RequestData;
import com.khmelenko.lab.travisclient.network.response.Requests;

import java.util.ArrayList;
import java.util.List;

/**
 * List adapter for Pull requests
 *
 * @author Dmytro Khmelenko
 */
public final class PullRequestsListAdapter extends RecyclerView.Adapter<BuildViewHolder> {

    private Requests mRequests;
    private List<RequestData> mPullRequests;
    private final OnListItemListener mListener;

    public PullRequestsListAdapter(Requests requests, OnListItemListener listener) {
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
    public BuildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_build_view, parent, false);
        return new BuildViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(BuildViewHolder holder, int position) {
        if (mRequests != null) {
            RequestData request = mPullRequests.get(position);
            bindPullRequest(holder, request);
        }
    }

    private void bindPullRequest(BuildViewHolder holder, RequestData request) {
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

        holder.mBuildView.setPullRequestTitle(request);
        holder.mBuildView.setCommit(relatedCommit);
        holder.mBuildView.setState(relatedBuild);
        holder.mBuildView.setTitle(
                holder.mBuildView.getContext().getString(R.string.pull_request_number, request.getPullRequestNumber()));
    }

    @Override
    public int getItemCount() {
        return mPullRequests.size();
    }

}
