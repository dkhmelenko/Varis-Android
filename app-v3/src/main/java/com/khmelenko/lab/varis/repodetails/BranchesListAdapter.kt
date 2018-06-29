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
class BranchesListAdapter(private var mBranches: Branches?,
                          private val mListener: (Int) -> Unit
) : RecyclerView.Adapter<BuildViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_build_view, parent, false)
        return BuildViewHolder(v, mListener)
    }

    override fun onBindViewHolder(holder: BuildViewHolder, position: Int) {
        if (mBranches != null) {
            val branch = mBranches!!.branches[position]
            val relatedCommit = mBranches!!.commits.firstOrNull { it.id == branch.commitId }

            holder.mBuildView.setState(branch)
            holder.mBuildView.setCommit(relatedCommit)
        }
    }

    override fun getItemCount(): Int {
        return if (mBranches != null) mBranches!!.branches.size else 0
    }

    /**
     * Sets branches
     *
     * @param branches Branches
     */
    fun setBranches(branches: Branches?) {
        mBranches = branches
    }
}
