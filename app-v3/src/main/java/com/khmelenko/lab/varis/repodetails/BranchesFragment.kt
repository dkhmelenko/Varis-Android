package com.khmelenko.lab.varis.repodetails

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.network.response.Branches
import kotlinx.android.synthetic.main.fragment_list_refreshable.list_refreshable_recycler_view
import kotlinx.android.synthetic.main.fragment_list_refreshable.list_refreshable_swipe_view
import kotlinx.android.synthetic.main.fragment_list_refreshable.progressbar
import kotlinx.android.synthetic.main.view_empty.empty_text

/**
 * Fragment with repository branches
 *
 * @author Dmytro Khmelenko
 */
class BranchesFragment : Fragment() {

    private lateinit var branchesListAdapter: BranchesListAdapter
    private var branches: Branches? = null

    private var listener: BranchesListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list_refreshable, container, false)

        list_refreshable_recycler_view.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        list_refreshable_recycler_view.layoutManager = layoutManager

        branchesListAdapter = BranchesListAdapter(branches, { position ->
            val branch = branches!!.branches[position]
            listener?.onBranchSelected(branch.id)
        })
        list_refreshable_recycler_view.adapter = branchesListAdapter

        list_refreshable_swipe_view.setColorSchemeResources(R.color.swipe_refresh_progress)
        list_refreshable_swipe_view.setOnRefreshListener { listener?.onReloadBranches() }

        progressbar.visibility = View.VISIBLE

        return view
    }

    /**
     * Checks whether data existing or not
     */
    private fun checkIfEmpty() {
        empty_text.setText(R.string.repo_details_branches_empty)
        if (branches == null || branches!!.branches.isEmpty()) {
            empty_text.visibility = View.VISIBLE
        } else {
            empty_text.visibility = View.GONE
        }
    }

    override fun onAttach(activity: Context?) {
        super.onAttach(activity)
        try {
            listener = activity as BranchesListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement BranchesListener")
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * Sets branches data
     *
     * @param branches Branches
     */
    fun setBranches(branches: Branches?) {
        list_refreshable_swipe_view.isRefreshing = false
        progressbar.visibility = View.GONE

        if (branches != null) {
            this.branches = branches
            branchesListAdapter.setBranches(this.branches)
            branchesListAdapter.notifyDataSetChanged()
        }

        checkIfEmpty()
    }

    /**
     * Interface for communication with this fragment
     */
    interface BranchesListener {

        /**
         * Handles selection of the branch
         *
         * @param buildId Id of the last build in branch
         */
        fun onBranchSelected(buildId: Long)

        /**
         * Handles request for reloading branches data
         */
        fun onReloadBranches()
    }

    companion object {

        /**
         * Creates new instance of the fragment
         *
         * @return Fragment instance
         */
        fun newInstance(): BranchesFragment {
            return BranchesFragment()
        }
    }

}
