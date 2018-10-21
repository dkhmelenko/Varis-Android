package com.khmelenko.lab.varis.repodetails

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.widget.Toast
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.activity.BaseActivity
import com.khmelenko.lab.varis.adapter.SmartFragmentStatePagerAdapter
import com.khmelenko.lab.varis.builddetails.BUILD_STATE_CHANGED
import com.khmelenko.lab.varis.builddetails.BuildDetailsActivity
import com.khmelenko.lab.varis.builddetails.EXTRA_BUILD_ID
import com.khmelenko.lab.varis.builddetails.EXTRA_REPO_SLUG
import com.khmelenko.lab.varis.network.response.Branches
import com.khmelenko.lab.varis.network.response.BuildHistory
import com.khmelenko.lab.varis.network.response.Requests
import dagger.android.AndroidInjection
import javax.inject.Inject

private const val BUILD_DETAILS_REQUEST_CODE = 0

const val REPO_SLUG_KEY = "RepoSlug"
const val RELOAD_REQUIRED_KEY = "ReloadRequiredKey"

private const val ITEMS_COUNT = 3
private const val INDEX_BUILD_HISTORY = 0
private const val INDEX_BRANCHES = 1
private const val INDEX_PULL_REQUESTS = 2

/**
 * Repository Details Activity
 *
 * @author Dmytro Khmelenko
 */
class RepoDetailsActivity : BaseActivity(), BuildHistoryFragment.BuildHistoryListener, BranchesFragment.BranchesListener, PullRequestsFragment.PullRequestsListener {

    @Inject
    lateinit var viewModelFactory: RepoDetailsViewModelFactory

    private lateinit var viewModel: RepoDetailsViewModel

    private var reloadRequired: Boolean = false
    private var initialLoad = true
    private lateinit var adapterViewPager: PagerAdapter

    /**
     * Custom adapter for view pager
     */
    private inner class PagerAdapter internal constructor(fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter(fragmentManager) {

        override fun getCount(): Int {
            return ITEMS_COUNT
        }

        override fun getItem(position: Int): Fragment? {
            return when (position) {
                INDEX_BUILD_HISTORY -> BuildHistoryFragment.newInstance()
                INDEX_BRANCHES -> BranchesFragment.newInstance()
                INDEX_PULL_REQUESTS -> PullRequestsFragment.newInstance()
                else -> null
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                INDEX_BUILD_HISTORY -> getString(R.string.repo_details_tab_build_history)
                INDEX_BRANCHES -> getString(R.string.repo_details_tab_branches)
                INDEX_PULL_REQUESTS -> getString(R.string.repo_details_tab_pull_requests)
                else -> null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_details)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RepoDetailsViewModel::class.java)

        initToolbar()

        // setting view pager
        val vpPager = findViewById<ViewPager>(R.id.repo_details_view_pager)
        adapterViewPager = PagerAdapter(supportFragmentManager)
        vpPager.adapter = adapterViewPager
        vpPager.offscreenPageLimit = ITEMS_COUNT

        val tabLayout = findViewById<TabLayout>(R.id.repo_details_view_tabs)
        tabLayout.setupWithViewPager(vpPager)

        init()
    }

    private fun init() {
        val repoSlug = intent.getStringExtra(REPO_SLUG_KEY)
        val projectName = repoSlug.substring(repoSlug.indexOf("/") + 1)
        title = projectName

        viewModel.repoSlug = repoSlug

        if (initialLoad || reloadRequired) {
            initialLoad = false
            viewModel.loadData()
        }

        viewModel.state().observe(this, Observer {
            when (it) {
                is RepoDetailsState.BuildHistoryLoaded -> updateBuildHistory(it.buildHistory)
                is RepoDetailsState.BuildHistoryError -> showBuildHistoryLoadingError(it.message)
                is RepoDetailsState.BranchesLoaded -> updateBranches(it.branches)
                is RepoDetailsState.BranchesError -> showBranchesLoadingError(it.message)
                is RepoDetailsState.PullRequestsLoaded -> updatePullRequests(it.pullRequests)
                is RepoDetailsState.PullRequestsError -> showPullRequestsLoadingError(it.message)
            }
        })
    }

    private fun updateBuildHistory(buildHistory: BuildHistory) {
        val fragment = adapterViewPager.getRegisteredFragment(INDEX_BUILD_HISTORY) as BuildHistoryFragment
        fragment.setBuildHistory(buildHistory)
    }

    private fun updateBranches(branches: Branches) {
        val fragment = adapterViewPager.getRegisteredFragment(INDEX_BRANCHES) as BranchesFragment
        fragment.setBranches(branches)
    }

    private fun updatePullRequests(requests: Requests) {
        val fragment = adapterViewPager.getRegisteredFragment(INDEX_PULL_REQUESTS) as PullRequestsFragment
        fragment.setPullRequests(requests)
    }

    private fun showBuildHistoryLoadingError(message: String?) {
        val fragment = adapterViewPager.getRegisteredFragment(INDEX_BUILD_HISTORY) as BuildHistoryFragment
        fragment.setBuildHistory(null)

        val msg = getString(R.string.error_failed_loading_build_history, message)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun showBranchesLoadingError(message: String?) {
        val fragment = adapterViewPager.getRegisteredFragment(INDEX_BRANCHES) as BranchesFragment
        fragment.setBranches(null)

        val msg = getString(R.string.error_failed_loading_branches, message)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun showPullRequestsLoadingError(message: String?) {
        val fragment = adapterViewPager.getRegisteredFragment(INDEX_PULL_REQUESTS) as PullRequestsFragment
        fragment.setPullRequests(null)

        val msg = getString(R.string.error_failed_loading_pull_requests, message)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * Initializes toolbar
     */
    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }
    }

    override fun onBuildSelected(buildId: Long) {
        goToBuildDetails(buildId)
    }

    override fun onReloadBuildHistory() {
        viewModel.loadBuildsHistory()
    }

    override fun onBranchSelected(buildId: Long) {
        goToBuildDetails(buildId)
    }

    override fun onReloadBranches() {
        viewModel.loadBranches()
    }

    override fun onPullRequestSelected(buildId: Long) {
        goToBuildDetails(buildId)
    }

    override fun onReloadPullRequests() {
        viewModel.loadRequests()
    }

    /**
     * Navigates to the build details
     *
     * @param buildId Build ID
     */
    private fun goToBuildDetails(buildId: Long) {
        val intent = Intent(this, BuildDetailsActivity::class.java)
        intent.putExtra(EXTRA_BUILD_ID, buildId)
        intent.putExtra(EXTRA_REPO_SLUG, viewModel.repoSlug)
        startActivityForResult(intent, BUILD_DETAILS_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BUILD_DETAILS_REQUEST_CODE && data != null) {
                reloadRequired = reloadRequired or data.getBooleanExtra(BUILD_STATE_CHANGED, false)
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(RELOAD_REQUIRED_KEY, reloadRequired)
        setResult(Activity.RESULT_OK, intent)

        super.onBackPressed()
    }
}
