package com.khmelenko.lab.varis.repositories;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khmelenko.lab.varis.R;
import com.khmelenko.lab.varis.adapter.OnListItemListener;
import com.khmelenko.lab.varis.adapter.viewholder.BuildViewHolder;
import com.khmelenko.lab.varis.network.response.Repo;

import java.util.List;

/**
 * Adapter class for the list of repositories
 *
 * @author Dmytro Khmelenko
 */
public final class RepoListAdapter extends RecyclerView.Adapter<BuildViewHolder> {

    private final List<Repo> mRepos;
    private final OnListItemListener mListener;

    public RepoListAdapter(List<Repo> repos, OnListItemListener listener) {
        mRepos = repos;
        mListener = listener;
    }

    @Override
    public BuildViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_build_view, viewGroup, false);
        return new BuildViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(BuildViewHolder repoViewHolder, int i) {
        Repo repo = mRepos.get(i);
        repoViewHolder.mBuildView.setTitle(repo.getSlug());
        repoViewHolder.mBuildView.setStateIndicator(repo.getLastBuildState());
        repoViewHolder.mBuildView.setFinishedAt(repo.getLastBuildFinishedAt());
        repoViewHolder.mBuildView.setDuration(repo.getLastBuildDuration());
    }

    @Override
    public int getItemCount() {
        return mRepos.size();
    }

}
