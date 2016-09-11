package com.khmelenko.lab.travisclient.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.khmelenko.lab.travisclient.fragment.LicensesDialogFragment;
import com.khmelenko.lab.travisclient.fragment.ReposFragment;
import com.khmelenko.lab.travisclient.mvp.MvpActivity;
import com.khmelenko.lab.travisclient.network.response.Repo;
import com.khmelenko.lab.travisclient.network.response.User;
import com.khmelenko.lab.travisclient.presenter.RepositoriesPresenter;
import com.khmelenko.lab.travisclient.storage.SearchHistoryProvider;
import com.khmelenko.lab.travisclient.view.RepositoriesView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Main application activity
 *
 * @author Dmytro Khmelenko
 */
public final class MainActivity extends MvpActivity<RepositoriesPresenter> implements RepositoriesView,
        ReposFragment.ReposFragmentListener {

    private static final int AUTH_ACTIVITY_CODE = 0;
    private static final int REPO_DETAILS_CODE = 1;

    private static final String SAVED_QUERY = "SavedQuery";

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Inject
    RepositoriesPresenter mPresenter;

    private ReposFragment mFragment;

    private SearchView mSearchView;

    private List<String> mQueryItems;

    private String mSavedQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        TravisApp.instance().activityInjector().inject(this);

        mFragment = (ReposFragment) getFragmentManager().findFragmentById(R.id.main_fragment);

        initToolbar();
        setupDrawerLayout();
    }

    @Override
    protected RepositoriesPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void attachPresenter() {
        getPresenter().attach(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_QUERY, mSearchView.getQuery().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSavedQuery = savedInstanceState.getString(SAVED_QUERY);
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
     * Sets up navigation drawer layout
     */
    private void setupDrawerLayout() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.drawer_login:
                        Intent loginIntent = new Intent(MainActivity.this, AuthActivity.class);
                        startActivityForResult(loginIntent, AUTH_ACTIVITY_CODE);
                        break;
                    case R.id.drawer_logout:
                        getPresenter().userLogout();

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
                    showProgress();
                    getPresenter().reloadRepos();
                    break;
                case REPO_DETAILS_CODE:
                    boolean reloadRequired = data.getBooleanExtra(RepoDetailsActivity.RELOAD_REQUIRED_KEY, false);
                    if (reloadRequired) {
                        showProgress();
                        getPresenter().reloadRepos();
                    }
                    break;
            }
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

            // restore query if it was
            if (!TextUtils.isEmpty(mSavedQuery)) {
                mSearchView.setQuery(mSavedQuery, false);
                mSearchView.setIconified(false);
            }
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

    @Override
    public void onRepositorySelected(Repo repo) {
        Intent intent = new Intent(MainActivity.this, RepoDetailsActivity.class);
        intent.putExtra(RepoDetailsActivity.REPO_SLUG_KEY, repo.getSlug());
        startActivityForResult(intent, REPO_DETAILS_CODE);
    }

    @Override
    public void onRefreshData() {
        getPresenter().reloadRepos();
    }

    @Override
    public void updateUserData(User user) {
        final NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        View header = view.getHeaderView(0);
        TextView usernameView = (TextView) header.findViewById(R.id.drawer_header_username);
        TextView emailView = (TextView) header.findViewById(R.id.drawer_header_email);

        if (user != null) {
            String username = user.getLogin();
            if (!TextUtils.isEmpty(user.getName())) {
                username = String.format("%1$s (%2$s)", user.getName(), user.getLogin());
            }
            usernameView.setText(username);
            emailView.setText(user.getEmail());

            // TODO Update image, when service will provide it
        } else {
            usernameView.setText(R.string.navigation_drawer_username_placeholder);
            emailView.setText(R.string.navigation_drawer_email_placeholder);
        }
    }

    @Override
    public void setRepos(List<Repo> repos) {
        mFragment.setRepos(repos);
    }

    @Override
    public void updateMenuState(@Nullable String accessToken) {
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        Menu menu = view.getMenu();
        if (TextUtils.isEmpty(accessToken)) {
            menu.findItem(R.id.drawer_login).setVisible(true);
            menu.findItem(R.id.drawer_logout).setVisible(false);
        } else {
            menu.findItem(R.id.drawer_login).setVisible(false);
            menu.findItem(R.id.drawer_logout).setVisible(true);
        }
    }

    @Override
    public void showError(String message) {
        mFragment.handleLoadingFailed(message);
    }

    @Override
    public void showProgress() {
        mFragment.setLoadingProgress(true);
    }

    @Override
    public void hideProgress() {
        mFragment.setLoadingProgress(false);
    }
}
