package com.khmelenko.lab.varis.repodetails

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.adapter.viewholder.BuildViewHolder
import com.khmelenko.lab.varis.network.response.BuildHistory

/**
 * Adapter for the list of builds
 *
 * @author Dmytro Khmelenko
 */
class BuildListAdapter(private var buildHistory: BuildHistory?,
                       private val listener: (Int) -> Unit) : RecyclerView.Adapter<BuildViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_build_view, parent, false)
        return BuildViewHolder(v, listener)
    }

    override fun onBindViewHolder(holder: BuildViewHolder, position: Int) {
        if (buildHistory != null) {
            val build = buildHistory!!.builds[position]
            val relatedCommit = buildHistory!!.commits.firstOrNull { it.id == build.commitId }
            holder.buildView.setState(build)
            holder.buildView.setCommit(relatedCommit)
        }
    }

    override fun getItemCount(): Int {
        return if (buildHistory != null) buildHistory!!.builds.size else 0
    }

    /**
     * Sets build history
     *
     * @param buildHistory Build history
     */
    fun setBuildHistory(buildHistory: BuildHistory?) {
        this.buildHistory = buildHistory
    }
}
