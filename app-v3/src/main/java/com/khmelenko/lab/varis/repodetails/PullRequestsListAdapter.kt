package com.khmelenko.lab.varis.repodetails

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.adapter.viewholder.BuildViewHolder
import com.khmelenko.lab.varis.network.response.RequestData
import com.khmelenko.lab.varis.network.response.Requests
import java.util.ArrayList

/**
 * List adapter for Pull requests
 *
 * @author Dmytro Khmelenko
 */
class PullRequestsListAdapter(private var requests: Requests?,
                              private val listener: (Int) -> Unit
) : RecyclerView.Adapter<BuildViewHolder>() {

    private var pullRequests: List<RequestData> = ArrayList()

    /**
     * Sets requests
     *
     * @param requests Requests
     */
    fun setRequests(requests: Requests?, pullRequests: List<RequestData>) {
        this.requests = requests
        this.pullRequests = pullRequests
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_build_view, parent, false)
        return BuildViewHolder(v, listener)
    }

    override fun onBindViewHolder(holder: BuildViewHolder, position: Int) {
        if (requests != null) {
            val request = pullRequests[position]
            bindPullRequest(holder, request)
        }
    }

    private fun bindPullRequest(holder: BuildViewHolder, request: RequestData) {
        val relatedCommit = requests!!.commits.firstOrNull { it.id == request.commitId }
        val relatedBuild = requests!!.builds.firstOrNull { it.id == request.buildId }

        holder.mBuildView.setPullRequestTitle(request)
        holder.mBuildView.setCommit(relatedCommit)
        holder.mBuildView.setState(relatedBuild)
        holder.mBuildView.setTitle(
                holder.mBuildView.context.getString(R.string.pull_request_number, request.pullRequestNumber))
    }

    override fun getItemCount(): Int {
        return pullRequests.size
    }
}
