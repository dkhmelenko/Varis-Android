package com.khmelenko.lab.varis.repodetails

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.network.response.BuildHistory
import kotlinx.android.synthetic.main.fragment_list_refreshable.list_refreshable_recycler_view
import kotlinx.android.synthetic.main.fragment_list_refreshable.list_refreshable_swipe_view
import kotlinx.android.synthetic.main.fragment_list_refreshable.progressbar
import kotlinx.android.synthetic.main.view_empty.empty_text

/**
 * Repository Build history
 *
 * @author Dmytro Khmelenko
 */
class BuildHistoryFragment : Fragment() {

    private lateinit var buildListAdapter: BuildListAdapter
    private var buildHistory: BuildHistory? = null

    private var listener: BuildHistoryListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_refreshable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list_refreshable_recycler_view.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        list_refreshable_recycler_view.layoutManager = layoutManager

        buildListAdapter = BuildListAdapter(buildHistory, { position ->
            val build = buildHistory!!.builds[position]
            listener?.onBuildSelected(build.id)
        })
        list_refreshable_recycler_view.adapter = buildListAdapter

        list_refreshable_swipe_view.setColorSchemeResources(R.color.swipe_refresh_progress)
        list_refreshable_swipe_view.setOnRefreshListener { listener?.onReloadBuildHistory() }

        progressbar.visibility = View.VISIBLE
    }


    /**
     * Checks whether data existing or not
     */
    private fun checkIfEmpty() {
        empty_text.setText(R.string.repo_details_builds_empty)
        if (buildHistory == null || buildHistory!!.builds.isEmpty()) {
            empty_text.visibility = View.VISIBLE
        } else {
            empty_text.visibility = View.GONE
        }
    }

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        try {
            listener = activity as BuildHistoryListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement BuildHistoryListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * Sets build history
     *
     * @param buildHistory Build history
     */
    fun setBuildHistory(buildHistory: BuildHistory?) {
        list_refreshable_swipe_view.isRefreshing = false
        progressbar.visibility = View.GONE

        if (buildHistory != null) {
            this.buildHistory = buildHistory
            buildListAdapter.setBuildHistory(this.buildHistory)
            buildListAdapter.notifyDataSetChanged()
        }

        checkIfEmpty()
    }

    /**
     * Interface for communication with this fragment
     */
    interface BuildHistoryListener {

        /**
         * Handles build selection
         *
         * @param buildNumber Build number
         */
        fun onBuildSelected(buildNumber: Long)

        /**
         * Handles reload data request
         */
        fun onReloadBuildHistory()
    }

    companion object {

        /**
         * Creates new instance of the fragment
         *
         * @return Fragment instance
         */
        fun newInstance(): BuildHistoryFragment {
            return BuildHistoryFragment()
        }
    }
}
