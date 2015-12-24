package com.khmelenko.lab.travisclient.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.common.Constants;
import com.khmelenko.lab.travisclient.event.travis.FindReposEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.UserSuccessEvent;
import com.khmelenko.lab.travisclient.fragment.ReposFragment;
import com.khmelenko.lab.travisclient.network.response.Repo;
import com.khmelenko.lab.travisclient.network.response.User;
import com.khmelenko.lab.travisclient.network.retrofit.RestClient;
import com.khmelenko.lab.travisclient.storage.AppSettings;
import com.khmelenko.lab.travisclient.storage.CacheStorage;
import com.khmelenko.lab.travisclient.task.TaskManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Main application activity
 *
 * @author Dmytro Khmelenko
 */
public class MainActivity extends AppCompatActivity implements ReposFragment.ReposFragmentListener {

    private static final int AUTH_ACTIVITY_CODE = 0;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private ReposFragment mFragment;

    private SearchView mSearchView;
    private TaskManager mTaskManager;
    private CacheStorage mCache;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFragment = (ReposFragment) getFragmentManager().findFragmentById(R.id.main_fragment);

        mTaskManager = new TaskManager();
        mCache = CacheStorage.newInstance();

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
        EventBus.getDefault().register(this);
        updateMenuState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
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
                        AppSettings.putAccessToken("");

                        // reset back to open source url
                        AppSettings.putServerUrl(Constants.OPEN_SOURCE_TRAVIS_URL);
                        RestClient.getInstance().updateTravisEndpoint(AppSettings.getServerUrl());

                        finish();
                        startActivity(getIntent());
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
     * Raised on loaded repositories
     *
     * @param event Event data
     */
    public void onEvent(FindReposEvent event) {
        mFragment.setLoadingProgress(false);
        mFragment.setRepos(event.getRepos());
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
        startActivity(intent);
    }

    @Override
    public void onRefreshData() {
        loadRepos();
    }
}
