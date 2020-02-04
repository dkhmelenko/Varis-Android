package com.khmelenko.lab.varis.repositories

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.adapter.viewholder.BuildViewHolder
import com.khmelenko.lab.varis.network.response.Repo

/**
 * Adapter class for the list of repositories
 *
 * @author Dmytro Khmelenko
 */
class RepoListAdapter(
        private val repos: List<Repo>,
        private val listener: (position: Int) -> Unit
) : RecyclerView.Adapter<BuildViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): BuildViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_build_view, viewGroup, false)
        return BuildViewHolder(v, listener)
    }

    override fun onBindViewHolder(repoViewHolder: BuildViewHolder, i: Int) {
        val repo = repos[i]
        repoViewHolder.buildView.setTitle(repo.slug)
        repoViewHolder.buildView.setStateIndicator(repo.lastBuildState)
        repoViewHolder.buildView.setFinishedAt(repo.lastBuildFinishedAt)
        repoViewHolder.buildView.setDuration(repo.lastBuildDuration)
    }

    override fun getItemCount(): Int {
        return repos.size
    }
}
