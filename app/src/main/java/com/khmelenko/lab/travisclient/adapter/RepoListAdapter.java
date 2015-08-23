package com.khmelenko.lab.travisclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.network.response.Repo;

import java.util.List;

/**
 * Adapter class for the list of repositories
 *
 * @author Dmytro Khmelenko
 */
public class RepoListAdapter extends RecyclerView.Adapter<RepoListAdapter.RepoViewHolder> {

    private final List<Repo> mRepos;

    public RepoListAdapter(List<Repo> repos) {
        mRepos = repos;
    }

    @Override
    public RepoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_repo, viewGroup, false);
        return new RepoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RepoViewHolder repoViewHolder, int i) {
        Repo record = mRepos.get(i);
        repoViewHolder.mName.setText(record.getSlug());
    }

    @Override
    public int getItemCount() {
        return mRepos.size();
    }

    /**
     * Viewholder class
     */
    class RepoViewHolder extends RecyclerView.ViewHolder {

        private TextView mName;

        public RepoViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            mName = (TextView) itemView.findViewById(R.id.item_repo_name);
        }
    }
}
