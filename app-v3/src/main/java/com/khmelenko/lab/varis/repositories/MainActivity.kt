package com.khmelenko.lab.varis.repositories

import android.app.Activity
import android.app.SearchManager
import androidx.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.about.AboutActivity
import com.khmelenko.lab.varis.about.LicensesDialogFragment
import com.khmelenko.lab.varis.activity.BaseActivity
import com.khmelenko.lab.varis.auth.AuthActivity
import com.khmelenko.lab.varis.network.response.Repo
import com.khmelenko.lab.varis.network.response.User
import com.khmelenko.lab.varis.repodetails.RELOAD_REQUIRED_KEY
import com.khmelenko.lab.varis.repodetails.REPO_SLUG_KEY
import com.khmelenko.lab.varis.repodetails.RepoDetailsActivity
import com.khmelenko.lab.varis.repositories.search.SearchResultsAdapter
import com.khmelenko.lab.varis.storage.SearchHistoryProvider
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.drawerLayout
import kotlinx.android.synthetic.main.activity_main.navigationView
import javax.inject.Inject

const val AUTH_ACTIVITY_CODE = 0
const val REPO_DETAILS_CODE = 1
const val SAVED_QUERY = "SavedQuery"
const val SEARCH_LIMIT = 3

/**
 * Main application activity
 *
 * @author Dmytro Khmelenko
 */
class MainActivity : BaseActivity(), ReposFragment.ReposFragmentListener {

    @Inject
    lateinit var viewModelFactory: RepositoriesViewModelFactory

    private lateinit var viewModel: RepositoriesViewModel

    private lateinit var fragment: ReposFragment

    private var searchView: SearchView? = null

    private var queryItems: List<String>? = null

    private var savedQuery: String? = null

    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, viewModelFactory).get(RepositoriesViewModel::class.java)
        viewModel.state().observe(this, Observer { repositoriesState ->
            hideProgress()
            when (repositoriesState) {
                is RepositoriesState.Loading -> showProgress()
                is RepositoriesState.Error -> showError(repositoriesState.message)
                is RepositoriesState.ReposList -> setRepos(repositoriesState.repos)
                is RepositoriesState.UserData -> updateUserData(repositoriesState.user)
            }
        })
        viewModel.init()

        fragment = fragmentManager.findFragmentById(R.id.main_fragment) as ReposFragment

        initToolbar()
        setupDrawerLayout()

        updateMenuState(viewModel.isUserLoggedIn())
    }

    override fun onDestroy() {
        viewModel.release()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (searchView != null) {
            outState.putString(SAVED_QUERY, searchView?.query.toString())
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedQuery = savedInstanceState.getString(SAVED_QUERY)
    }

    /**
     * Initializes toolbar
     */
    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        drawerToggle.syncState()
    }

    /**
     * Sets up navigation drawer layout
     */
    private fun setupDrawerLayout() {
        drawerLayout.addDrawerListener(drawerToggle)
        val view = findViewById<NavigationView>(R.id.navigationView)
        view.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.drawer_login -> {
                    val loginIntent = Intent(this@MainActivity, AuthActivity::class.java)
                    startActivityForResult(loginIntent, AUTH_ACTIVITY_CODE)
                }
                R.id.drawer_logout -> {
                    viewModel.userLogout()

                    finish()
                    startActivity(intent)
                }
                R.id.drawer_licenses -> {
                    val dialog = LicensesDialogFragment.newInstance()
                    dialog.show(supportFragmentManager, "LicensesDialog")
                }
                R.id.drawer_about -> {
                    val aboutIntent = Intent(this@MainActivity, AboutActivity::class.java)
                    startActivity(aboutIntent)
                }
            }
            menuItem.isChecked = false
            drawerLayout.closeDrawers()
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                AUTH_ACTIVITY_CODE -> {
                    // clear previous data
                    fragment.clearData()
                    showProgress()
                    viewModel.reloadRepos()
                }
                REPO_DETAILS_CODE -> {
                    val reloadRequired = data?.getBooleanExtra(RELOAD_REQUIRED_KEY, false) ?: false
                    if (reloadRequired) {
                        showProgress()
                        viewModel.reloadRepos()
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView = null
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }
        if (searchView != null) {
            searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String): Boolean {
                    var submitProhibited = true
                    if (query.length > SEARCH_LIMIT) {
                        // save search query to history
                        val suggestionsProvider = SearchRecentSuggestions(applicationContext,
                                SearchHistoryProvider.AUTHORITY, SearchHistoryProvider.MODE)
                        suggestionsProvider.saveRecentQuery(query, null)
                        submitProhibited = false
                    }
                    return submitProhibited
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    reloadSearchHistoryAdapter(newText)
                    return true
                }

            })
            searchView?.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
                override fun onSuggestionClick(position: Int): Boolean {
                    searchView?.setQuery(queryItems!![position], true)
                    return true
                }

                override fun onSuggestionSelect(position: Int): Boolean {
                    return true
                }
            })

            reloadSearchHistoryAdapter("")

            // restore query if it was
            if (!TextUtils.isEmpty(savedQuery)) {
                searchView?.setQuery(savedQuery, false)
                searchView?.isIconified = false
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (searchView != null && !searchView!!.isIconified) {
            searchView?.setQuery("", false)
            searchView?.isIconified = true
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Reloads search history adapter
     *
     * @param query Query
     */
    private fun reloadSearchHistoryAdapter(query: String) {
        val cursor = SearchHistoryProvider.queryRecentSearch(this, query)
        queryItems = SearchHistoryProvider.transformSearchResultToList(cursor)

        searchView?.suggestionsAdapter = SearchResultsAdapter(this, cursor)
    }

    override fun onRepositorySelected(repo: Repo) {
        val intent = Intent(this@MainActivity, RepoDetailsActivity::class.java)
        intent.putExtra(REPO_SLUG_KEY, repo.slug)
        startActivityForResult(intent, REPO_DETAILS_CODE)
    }

    override fun onRefreshData() {
        viewModel.reloadRepos()
    }

    private fun updateUserData(user: User?) {
        val view = findViewById<NavigationView>(R.id.navigationView)
        val header = view.getHeaderView(0)
        val usernameView = header.findViewById<TextView>(R.id.drawer_header_username)
        val emailView = header.findViewById<TextView>(R.id.drawer_header_email)

        if (user != null) {
            var username = user.login
            if (!TextUtils.isEmpty(user.name)) {
                username = String.format("%1\$s (%2\$s)", user.name, user.login)
            }
            usernameView.text = username
            emailView.text = user.email

            // TODO Update image, when service will provide it
        } else {
            usernameView.setText(R.string.navigation_drawer_username_placeholder)
            emailView.setText(R.string.navigation_drawer_email_placeholder)
        }
    }

    private fun setRepos(repos: List<Repo>) {
        fragment.setRepos(repos)
    }

    private fun updateMenuState(userLoggedIn: Boolean) {
        val menu = navigationView.menu
        if (userLoggedIn) {
            menu.findItem(R.id.drawer_login).isVisible = false
            menu.findItem(R.id.drawer_logout).isVisible = true
        } else {
            menu.findItem(R.id.drawer_login).isVisible = true
            menu.findItem(R.id.drawer_logout).isVisible = false
        }
    }

    private fun showError(message: String?) {
        fragment.handleLoadingFailed(message)
    }

    private fun showProgress() {
        fragment.setLoadingProgress(true)
    }

    fun hideProgress() {
        fragment.setLoadingProgress(false)
    }
}
