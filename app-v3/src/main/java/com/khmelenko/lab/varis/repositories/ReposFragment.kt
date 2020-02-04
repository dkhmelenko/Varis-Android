package com.khmelenko.lab.varis.repositories

import android.app.Fragment
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.network.response.Repo
import kotlinx.android.synthetic.main.fragment_repos.mainReposRecyclerView
import kotlinx.android.synthetic.main.fragment_repos.mainReposSwipeView
import kotlinx.android.synthetic.main.view_empty.empty_text
import java.util.ArrayList

/**
 * Fragment for main app screen
 *
 * @author Dmytro Khmelenko
 */
class ReposFragment : Fragment() {

    private var listener: ReposFragmentListener? = null

    private lateinit var repoListAdapter: RepoListAdapter
    private val repos = ArrayList<Repo>()

    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_repos, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainReposRecyclerView.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(activity)
        mainReposRecyclerView.layoutManager = layoutManager

        repoListAdapter = RepoListAdapter(repos) { position ->
            listener?.onRepositorySelected(repos[position])
        }
        mainReposRecyclerView.adapter = repoListAdapter

        mainReposSwipeView.setColorSchemeResources(R.color.swipe_refresh_progress)
        mainReposSwipeView.setOnRefreshListener {
            listener?.onRefreshData()
        }
    }

    /**
     * Checks whether data existing or not
     */
    private fun checkIfEmpty() {
        empty_text.setText(R.string.repo_empty_text)
        if (repos.isEmpty()) {
            empty_text.visibility = View.VISIBLE
        } else {
            empty_text.visibility = View.GONE
        }
    }

    /**
     * Clears the fragment data
     */
    fun clearData() {
        repos.clear()
        repoListAdapter.notifyDataSetChanged()
    }

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        try {
            listener = activity as ReposFragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement MainFragmentListener")
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * Sets the list of repositories
     *
     * @param repos Repositories
     */
    fun setRepos(repos: List<Repo>) {
        this.repos.clear()
        this.repos.addAll(repos)
        repoListAdapter.notifyDataSetChanged()

        checkIfEmpty()
    }

    /**
     * Sets the progress of the loading
     *
     * @param isLoading True, if loading is in progress. False otherwise
     */
    fun setLoadingProgress(isLoading: Boolean) {
        if (isLoading) {
            if (progressDialog == null) {
                progressDialog = ProgressDialog.show(activity, "", getString(R.string.loading_msg))
            }
        } else {
            mainReposSwipeView.isRefreshing = false
            progressDialog?.dismiss()
        }
    }

    /**
     * Handles the case when loading data failed
     */
    fun handleLoadingFailed(message: String?) {
        checkIfEmpty()

        val msg = getString(R.string.error_failed_loading_repos, message)
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * Fragment listener
     */
    interface ReposFragmentListener {

        /**
         * Handles repository selection
         *
         * @param repo Selected repository
         */
        fun onRepositorySelected(repo: Repo)

        /**
         * Handles request for refreshing data
         */
        fun onRefreshData()
    }

    companion object {

        /**
         * Creates new instance of the fragment
         *
         * @return Fragment instance
         */
        fun newInstance(): ReposFragment {
            return ReposFragment()
        }
    }
}
