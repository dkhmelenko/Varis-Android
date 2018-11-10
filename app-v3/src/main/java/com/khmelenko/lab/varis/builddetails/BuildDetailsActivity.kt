package com.khmelenko.lab.varis.builddetails

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.activity.BaseActivity
import com.khmelenko.lab.varis.converter.BuildStateHelper
import com.khmelenko.lab.varis.log.LogEntryComponent
import com.khmelenko.lab.varis.network.response.BuildDetails
import com.khmelenko.lab.varis.network.response.Job
import com.khmelenko.lab.varis.widget.BuildView
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_build_details.build_details_build_data
import kotlinx.android.synthetic.main.activity_build_details.build_details_layout
import kotlinx.android.synthetic.main.activity_build_details.build_details_scroll_btn
import kotlinx.android.synthetic.main.activity_build_details.build_details_scroll_up_btn
import kotlinx.android.synthetic.main.activity_build_details.progressbarview
import javax.inject.Inject

const val EXTRA_REPO_SLUG = "RepoSlug"
const val EXTRA_BUILD_ID = "BuildId"
const val BUILD_STATE_CHANGED = "BuildsStateChanged"

private const val RAW_LOG_FRAGMENT_TAG = "RawLogFragment"
private const val JOBS_FRAGMENT_TAG = "JobsFragment"

/**
 * Build details
 *
 * @author Dmytro Khmelenko
 */
class BuildDetailsActivity : BaseActivity(), JobsFragment.JobsListener, RawLogFragment.OnRawLogFragmentListener {

    @Inject
    lateinit var viewModelFactory: BuildDetailsViewModelFactory
    private lateinit var viewModel: BuildsDetailsViewModel

    private lateinit var jobsFragment: JobsFragment
    private lateinit var rawLogFragment: RawLogFragment

    private var canContributeToRepo: Boolean = false
    private var buildInProgressState: Boolean = false
    private var buildStateChanged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_build_details)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BuildsDetailsViewModel::class.java)
        viewModel.state().observe(this, Observer {
            handleState(it!!)
        })

        initToolbar()
        init()
    }

    private fun handleState(state: BuildDetailsState) {
        hideProgress()
        when(state) {
            is BuildDetailsState.Loading -> showProgress()
            is BuildDetailsState.Error -> showLoadingError(state.message)
            is BuildDetailsState.BuildAdditionalActionsAvailable -> showAdditionalActionsForBuild(state.details)
            is BuildDetailsState.BuildLogsLoaded -> showBuildLogs()
            is BuildDetailsState.BuildJobsLoaded -> showBuildJobs(state.jobs)
            is BuildDetailsState.BuildDetailsLoaded -> updateBuildDetails(state.details)
            is BuildDetailsState.LogError -> showLogError()
            is BuildDetailsState.LogEntryLoaded -> setLog(state.logEntry)
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        if (::jobsFragment.isInitialized) {
            detachFragment(jobsFragment)
        }
        if (::rawLogFragment.isInitialized) {
            detachFragment(rawLogFragment)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_build_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val itemCancel = menu.findItem(R.id.build_activity_action_cancel)
        val itemRestart = menu.findItem(R.id.build_activity_action_restart)
        if (canContributeToRepo) {
            if (buildInProgressState) {
                itemCancel.isVisible = true
            } else {
                itemRestart.isVisible = true
            }
        } else {
            itemCancel.isVisible = false
            itemRestart.isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.build_activity_action_restart -> {
                viewModel.restartBuild()
                handleBuildAction()
                return true
            }
            R.id.build_activity_action_cancel -> {
                viewModel.cancelBuild()
                handleBuildAction()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onJobSelected(job: Job) {
        if (!::rawLogFragment.isInitialized) {
            rawLogFragment = RawLogFragment.newInstance()
        }
        replaceFragment(R.id.build_details_container, rawLogFragment, RAW_LOG_FRAGMENT_TAG, null)

        viewModel.startLoadingLog(job.id)
    }

    override fun onLogLoaded() {
        build_details_scroll_btn.show()
    }

    private fun scrollContentDown() {
        build_details_scroll_btn.hide()
        build_details_scroll_up_btn.show()

        build_details_layout.post { build_details_layout.fullScroll(View.FOCUS_DOWN) }
    }

    private fun scrollContentUp() {
        build_details_scroll_btn.show()
        build_details_scroll_up_btn.hide()

        build_details_layout.post { build_details_layout.fullScroll(View.FOCUS_UP) }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(BUILD_STATE_CHANGED, buildStateChanged)
        setResult(Activity.RESULT_OK, intent)

        super.onBackPressed()
        build_details_scroll_btn.hide()
        build_details_scroll_up_btn.hide()
    }

    private fun init() {
        val intent = intent
        val action = intent.action

        var intentUrl: String? = null
        var repoSlug = ""
        var buildId = 0L
        if (Intent.ACTION_VIEW == action) {
            intentUrl = intent.dataString
        } else {
            repoSlug = getIntent().getStringExtra(EXTRA_REPO_SLUG)
            buildId = getIntent().getLongExtra(EXTRA_BUILD_ID, 0L)
        }

        build_details_scroll_btn.setOnClickListener { scrollContentDown() }
        build_details_scroll_up_btn.setOnClickListener { scrollContentUp() }

        viewModel.startLoadingData(intentUrl, repoSlug, buildId)
    }

    /**
     * Shows additional actions for build
     *
     * @param details Build details
     */
    private fun showAdditionalActionsForBuild(details: BuildDetails) {

        // check whether the user can contribute to this repo
        canContributeToRepo = viewModel.canUserContributeToRepo()
        if (canContributeToRepo) {
            buildInProgressState = BuildStateHelper.isInProgress(details.build.state)
            invalidateOptionsMenu()
        }
    }

    private fun showProgress() {
        progressbarview.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progressbarview.visibility = View.GONE
    }

    private fun showLoadingError(message: String?) {
        val msg = getString(R.string.error_failed_loading_build_details, message)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun updateBuildDetails(buildDetails: BuildDetails?) {
        if (buildDetails != null) {
            build_details_build_data.visibility = View.VISIBLE
            showBuildDetails(buildDetails)
        } else {
            build_details_build_data.visibility = View.GONE
        }
        checkIfEmpty(buildDetails)
    }

    private fun showLogError() {
        rawLogFragment.showProgress(false)
        rawLogFragment.showError(true)
    }

    private fun setLog(log: LogEntryComponent) {
        rawLogFragment.showLog(log)
    }

    private fun showBuildJobs(jobs: List<Job>) {
        if (!::jobsFragment.isInitialized) {
            jobsFragment = JobsFragment.newInstance(jobs)
        } else {
            jobsFragment.setJobs(jobs)
        }
        addFragment(R.id.build_details_container, jobsFragment, JOBS_FRAGMENT_TAG)
    }

    private fun showBuildLogs() {
        if (!::rawLogFragment.isInitialized) {
            rawLogFragment = RawLogFragment.newInstance()
        }
        addFragment(R.id.build_details_container, rawLogFragment, RAW_LOG_FRAGMENT_TAG)
    }

    /**
     * Handles build action
     */
    private fun handleBuildAction() {
        showProgress()

        canContributeToRepo = false
        invalidateOptionsMenu()

        buildStateChanged = true
    }

    /**
     * Initializes toolbar
     */
    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * Shows build details
     *
     * @param details Build details
     */
    private fun showBuildDetails(details: BuildDetails) {
        val build = details.build
        val commit = details.commit

        val buildView = findViewById<BuildView>(R.id.build_details_build_data)
        buildView.setState(build)
        buildView.setCommit(commit)
    }

    /**
     * Checks whether data existing or not
     */
    private fun checkIfEmpty(details: BuildDetails?) {
        val emptyText = findViewById<TextView>(R.id.empty_text)
        emptyText.setText(R.string.build_details_empty)
        if (details == null) {
            emptyText.visibility = View.VISIBLE
        } else {
            emptyText.visibility = View.GONE
        }
    }
}
