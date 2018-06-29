package com.khmelenko.lab.varis.repodetails

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.network.response.RequestData
import com.khmelenko.lab.varis.network.response.Requests
import kotlinx.android.synthetic.main.fragment_list_refreshable.list_refreshable_recycler_view
import kotlinx.android.synthetic.main.fragment_list_refreshable.list_refreshable_swipe_view
import kotlinx.android.synthetic.main.fragment_list_refreshable.progressbar
import kotlinx.android.synthetic.main.view_empty.empty_text
import java.util.ArrayList

/**
 * Repository pull requests
 *
 * @author Dmytro Khmelenko
 */
class PullRequestsFragment : Fragment() {

    private lateinit var pullRequestsListAdapter: PullRequestsListAdapter
    private var requests: Requests? = null
    private var pullRequests: List<RequestData> = ArrayList()

    private var listener: PullRequestsListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list_refreshable, container, false)

        list_refreshable_recycler_view.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        list_refreshable_recycler_view.layoutManager = layoutManager

        pullRequestsListAdapter = PullRequestsListAdapter(requests, { position ->
            val requestData = pullRequests[position]
            listener?.onPullRequestSelected(requestData.buildId)
        })
        list_refreshable_recycler_view.adapter = pullRequestsListAdapter

        list_refreshable_swipe_view.setColorSchemeResources(R.color.swipe_refresh_progress)
        list_refreshable_swipe_view.setOnRefreshListener { listener!!.onReloadPullRequests() }

        progressbar.visibility = View.VISIBLE

        return view
    }

    /**
     * Fetches pull requests
     */
    private fun fetchPullRequests(requests: Requests): List<RequestData> {
        val pullRequest = ArrayList<RequestData>()
        for (request in requests.requests) {
            if (request.isPullRequest && !pullRequest.contains(request)) {
                pullRequest.add(request)
            }
        }
        return pullRequest
    }

    /**
     * Checks whether data existing or not
     */
    private fun checkIfEmpty() {
        empty_text.setText(R.string.repo_details_pull_request_empty)
        if (pullRequests.isEmpty()) {
            empty_text.visibility = View.VISIBLE
        } else {
            empty_text.visibility = View.GONE
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listener = context as PullRequestsListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(context!!.toString() + " must implement PullRequestsListener")
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * Sets pull request data
     *
     * @param requests Pull requests
     */
    fun setPullRequests(requests: Requests?) {
        list_refreshable_swipe_view.isRefreshing = false
        progressbar.visibility = View.GONE

        if (requests != null) {
            this.requests = requests
            pullRequests = fetchPullRequests(requests)
            pullRequestsListAdapter.setRequests(requests, pullRequests)
            pullRequestsListAdapter.notifyDataSetChanged()
        }

        checkIfEmpty()
    }

    /**
     * Interface for communication with this fragment
     */
    interface PullRequestsListener {

        /**
         * Handles selection of the pull request
         *
         * @param buildId ID of the build of the pull request
         */
        fun onPullRequestSelected(buildId: Long)

        /**
         * Handles reload action for pull request
         */
        fun onReloadPullRequests()
    }

    companion object {

        /**
         * Creates new instance of the fragment
         *
         * @return Fragment instance
         */
        fun newInstance(): PullRequestsFragment {
            return PullRequestsFragment()
        }
    }
}
