package com.khmelenko.lab.travisclient.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.TravisApp;
import com.khmelenko.lab.travisclient.adapter.SearchResultsAdapter;
import com.khmelenko.lab.travisclient.common.Constants;
import com.khmelenko.lab.travisclient.event.travis.FindReposEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.UserSuccessEvent;
import com.khmelenko.lab.travisclient.fragment.LicensesDialogFragment;
import com.khmelenko.lab.travisclient.fragment.ReposFragment;
import com.khmelenko.lab.travisclient.network.response.Repo;
import com.khmelenko.lab.travisclient.network.response.User;
import com.khmelenko.lab.travisclient.network.retrofit.RestClient;
import com.khmelenko.lab.travisclient.storage.AppSettings;
import com.khmelenko.lab.travisclient.storage.CacheStorage;
import com.khmelenko.lab.travisclient.storage.SearchHistoryProvider;
import com.khmelenko.lab.travisclient.task.TaskManager;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Main application activity
 *
 * @author Dmytro Khmelenko
 */
public final class MainActivity extends BaseActivity implements ReposFragment.ReposFragmentListener {

    private static final int AUTH_ACTIVITY_CODE = 0;
    private static final int REPO_DETAILS_CODE = 1;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private ReposFragment mFragment;

    private SearchView mSearchView;

    @Inject
    RestClient mRestClient;
    @Inject
    EventBus mEventBus;
    @Inject
    TaskManager mTaskManager;
    @Inject
    CacheStorage mCache;

    private User mUser;
    private List<String> mQueryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        TravisApp.instance().activityInjector().inject(this);

        mFragment = (ReposFragment) getFragmentManager().findFragmentById(R.id.main_fragment);

        initToolbar();
        setupDrawerLayout();

        mUser = mCache.restoreUser();
        updateNavigationViewData();

        mFragment.setLoadingProgress(true);
        loadRepos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEventBus.register(this);
        updateMenuState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEventBus.unregister(this);
        mFragment.setLoadingProgress(false);
    }

    /**
     * Initializes toolbar
     */
    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Starts loading repositories
     */
    private void loadRepos() {
        String accessToken = AppSettings.getAccessToken();
        if (TextUtils.isEmpty(accessToken)) {
            mTaskManager.findRepos(null);
        } else {
            mTaskManager.getUser();
        }
    }

    /**
     * Sets up navigation drawer layout
     */
    private void setupDrawerLayout() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.drawer_login:
                        Intent loginIntent = new Intent(MainActivity.this, AuthActivity.class);
                        startActivityForResult(loginIntent, AUTH_ACTIVITY_CODE);
                        break;
                    case R.id.drawer_logout:
                        // clear user data
                        mCache.deleteUser();
                        mCache.deleteRepos();
                        AppSettings.putAccessToken("");

                        // reset back to open source url
                        AppSettings.putServerUrl(Constants.OPEN_SOURCE_TRAVIS_URL);
                        mRestClient.updateTravisEndpoint(AppSettings.getServerUrl());

                        finish();
                        startActivity(getIntent());
                        break;
                    case R.id.drawer_licenses:
                        LicensesDialogFragment dialog = LicensesDialogFragment.newInstance();
                        dialog.show(getSupportFragmentManager(), "LicensesDialog");
                        break;
                    case R.id.drawer_about:
                        Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(aboutIntent);
                        break;
                }
                menuItem.setChecked(false);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AUTH_ACTIVITY_CODE:
                    // clear previous data
                    mFragment.clearData();
                    mFragment.setLoadingProgress(true);
                    loadRepos();
                    break;
                case REPO_DETAILS_CODE:
                    boolean reloadRequired = data.getBooleanExtra(RepoDetailsActivity.RELOAD_REQUIRED_KEY, false);
                    if (reloadRequired) {
                        mFragment.setLoadingProgress(true);
                        loadRepos();
                    }
                    break;
            }
        }
    }

    /**
     * Updates menu state
     */
    private void updateMenuState() {
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        Menu menu = view.getMenu();
        String accessToken = AppSettings.getAccessToken();
        if (TextUtils.isEmpty(accessToken)) {
            menu.findItem(R.id.drawer_login).setVisible(true);
            menu.findItem(R.id.drawer_logout).setVisible(false);
        } else {
            menu.findItem(R.id.drawer_login).setVisible(false);
            menu.findItem(R.id.drawer_logout).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mSearchView = null;
        if (searchItem != null) {
            mSearchView = (SearchView) searchItem.getActionView();
        }
        if (mSearchView != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    // save search query to history
                    SearchRecentSuggestions suggestionsProvider = new SearchRecentSuggestions(MainActivity.this,
                            SearchHistoryProvider.AUTHORITY, SearchHistoryProvider.MODE);
                    suggestionsProvider.saveRecentQuery(query, null);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    reloadSearchHistoryAdapter(newText);
                    return true;
                }

            });
            mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionClick(int position) {
                    mSearchView.setQuery(mQueryItems.get(position), true);
                    return true;
                }

                @Override
                public boolean onSuggestionSelect(int position) {
                    return true;
                }
            });
            reloadSearchHistoryAdapter("");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (!mSearchView.isIconified()) {
            mSearchView.setQuery("", false);
            mSearchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Reloads search history adapter
     *
     * @param query Query
     */
    private void reloadSearchHistoryAdapter(String query) {
        Cursor cursor = SearchHistoryProvider.queryRecentSearch(this, query);
        mQueryItems = SearchHistoryProvider.transformSearchResultToList(cursor);

        mSearchView.setSuggestionsAdapter(new SearchResultsAdapter(this, cursor));
    }

    /**
     * Raised on loaded repositories
     *
     * @param event Event data
     */
    public void onEvent(FindReposEvent event) {
        mFragment.setLoadingProgress(false);
        mFragment.setRepos(event.getRepos());

        mCache.saveRepos(event.getRepos());
    }

    /**
     * Raised on failed loading data
     *
     * @param event Event data
     */
    public void onEvent(LoadingFailedEvent event) {
        mFragment.setLoadingProgress(false);
        mFragment.handleLoadingFailed(event.getTaskError().getMessage());
    }

    /**
     * Raised on loaded user information
     *
     * @param event Event data
     */
    public void onEvent(UserSuccessEvent event) {
        mUser = event.getUser();

        // cache user data
        mCache.saveUser(mUser);

        updateNavigationViewData();

        String loginName = mUser.getLogin();
        mTaskManager.userRepos(loginName);
    }

    /**
     * Updates user data in navigation view
     */
    private void updateNavigationViewData() {
        final NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        View header = view.getHeaderView(0);
        TextView usernameView = (TextView) header.findViewById(R.id.drawer_header_username);
        TextView emailView = (TextView) header.findViewById(R.id.drawer_header_email);

        if (mUser != null) {
            String username = mUser.getLogin();
            if (!TextUtils.isEmpty(mUser.getName())) {
                username = String.format("%1$s (%2$s)", mUser.getName(), mUser.getLogin());
            }
            usernameView.setText(username);
            emailView.setText(mUser.getEmail());

            // TODO Update image, when service will provide it
        } else {
            usernameView.setText(R.string.navigation_drawer_username_placeholder);
            emailView.setText(R.string.navigation_drawer_email_placeholder);
        }
    }

    @Override
    public void onRepositorySelected(Repo repo) {
        Intent intent = new Intent(MainActivity.this, RepoDetailsActivity.class);
        intent.putExtra(RepoDetailsActivity.REPO_SLUG_KEY, repo.getSlug());
        startActivityForResult(intent, REPO_DETAILS_CODE);
    }

    @Override
    public void onRefreshData() {
        loadRepos();
    }
}
