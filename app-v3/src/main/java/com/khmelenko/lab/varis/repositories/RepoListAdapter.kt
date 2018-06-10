package com.khmelenko.lab.varis.repositories

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.adapter.OnListItemListener
import com.khmelenko.lab.varis.adapter.viewholder.BuildViewHolder
import com.khmelenko.lab.varis.network.response.Repo

/**
 * Adapter class for the list of repositories
 *
 * @author Dmytro Khmelenko
 */
class RepoListAdapter(
        private val mRepos: List<Repo>,
        private val mListener: OnListItemListener
) : RecyclerView.Adapter<BuildViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): BuildViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_build_view, viewGroup, false)
        return BuildViewHolder(v, mListener)
    }

    override fun onBindViewHolder(repoViewHolder: BuildViewHolder, i: Int) {
        val repo = mRepos[i]
        repoViewHolder.mBuildView.setTitle(repo.slug)
        repoViewHolder.mBuildView.setStateIndicator(repo.lastBuildState)
        repoViewHolder.mBuildView.setFinishedAt(repo.lastBuildFinishedAt)
        repoViewHolder.mBuildView.setDuration(repo.lastBuildDuration)
    }

    override fun getItemCount(): Int {
        return mRepos.size
    }
}
