package com.khmelenko.lab.travisclient.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.network.response.BuildHistory;
import com.khmelenko.lab.travisclient.network.response.Commit;
import com.khmelenko.lab.travisclient.view.BuildView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter for the list of builds
 *
 * @author Dmytro Khmelenko
 */
public class BuildListAdapter extends RecyclerView.Adapter<BuildListAdapter.BuildViewHolder> {

    private BuildHistory mBuildHistory;
    private final Context mContext;
    private final OnListItemListener mListener;

    public BuildListAdapter(Context context, BuildHistory buildHistory, OnListItemListener listener) {
        mContext = context;
        mBuildHistory = buildHistory;
        mListener = listener;
    }

    @Override
    public BuildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_build, parent, false);
        return new BuildViewHolder(v);
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
            holder.mBuildView.setBuildData(build, relatedCommit);
        }
    }

    @Override
    public int getItemCount() {
        return mBuildHistory != null ? mBuildHistory.getBuilds().size() : 0;
    }

    public void setBuildHistory(BuildHistory buildHistory) {
        mBuildHistory = buildHistory;
    }

    /**
     * Viewholder class
     */
    class BuildViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.item_build_card_view)
        View mParent;

        @Bind(R.id.item_build_data)
        BuildView mBuildView;

        public BuildViewHolder(View itemView) {
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
