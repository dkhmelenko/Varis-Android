package com.khmelenko.lab.varis.repositories.search

import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.activity.BaseActivity
import com.khmelenko.lab.varis.network.response.Repo
import com.khmelenko.lab.varis.repodetails.REPO_SLUG_KEY
import com.khmelenko.lab.varis.repodetails.RepoDetailsActivity
import com.khmelenko.lab.varis.repodetails.RepoDetailsViewModelFactory
import com.khmelenko.lab.varis.repositories.ReposFragment
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * Provides activity with search results
 *
 * @author Dmytro Khmelenko
 */
class SearchResultsActivity : BaseActivity(), ReposFragment.ReposFragmentListener {

    @Inject
    lateinit var viewModelFactory: RepoDetailsViewModelFactory

    private lateinit var viewModel: SearchResultsViewModel

    private lateinit var fragment: ReposFragment

    private var searchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchResultsViewModel::class.java)
        viewModel.state().observe(this, Observer { state ->
            hideProgress()
            when (state) {
                is SearchState.Loading -> showProgress()
                is SearchState.ReposList -> setSearchResults(state.repos)
                is SearchState.Error -> showLoadingError(state.message)
            }
        })

        fragment = fragmentManager.findFragmentById(R.id.search_fragment_repos) as ReposFragment

        initToolbar()

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        handleIntent(intent)
    }

    /**
     * Handles received intent
     *
     * @param intent Intent
     */
    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            searchQuery = query
            showProgress()
            viewModel.startRepoSearch(query)
        }
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
            toolbar.setNavigationOnClickListener { v -> onBackPressed() }
        }
    }

    override fun onRepositorySelected(repo: Repo) {
        val intent = Intent(this@SearchResultsActivity, RepoDetailsActivity::class.java)
        intent.putExtra(REPO_SLUG_KEY, repo.slug)
        startActivity(intent)
    }

    override fun onRefreshData() {
        viewModel.startRepoSearch(searchQuery!!)
    }

    private fun showProgress() {
        fragment.setLoadingProgress(true)
    }

    private fun hideProgress() {
        fragment.setLoadingProgress(false)
    }

    private fun setSearchResults(repos: List<Repo>) {
        fragment.setRepos(repos)
    }

    private fun showLoadingError(message: String?) {
        fragment.handleLoadingFailed(message)
    }
}
