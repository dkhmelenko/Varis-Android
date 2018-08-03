package com.khmelenko.lab.varis.repodetails

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.adapter.viewholder.BuildViewHolder
import com.khmelenko.lab.varis.network.response.Branches

/**
 * List adapter for branches
 *
 * @author Dmytro Khmelenko
 */
class BranchesListAdapter(private var branches: Branches?,
                          private val listener: (Int) -> Unit
) : RecyclerView.Adapter<BuildViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_build_view, parent, false)
        return BuildViewHolder(v, listener)
    }

    override fun onBindViewHolder(holder: BuildViewHolder, position: Int) {
        if (branches != null) {
            val branch = branches!!.branches[position]
            val relatedCommit = branches!!.commits.firstOrNull { it.id == branch.commitId }

            holder.buildView.setState(branch)
            holder.buildView.setCommit(relatedCommit)
        }
    }

    override fun getItemCount(): Int {
        return if (branches != null) branches!!.branches.size else 0
    }

    /**
     * Sets branches
     *
     * @param branches Branches
     */
    fun setBranches(branches: Branches?) {
        this.branches = branches
    }
}
