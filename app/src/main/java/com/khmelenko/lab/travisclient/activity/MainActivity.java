package com.khmelenko.lab.travisclient.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.adapter.OnListItemListener;
import com.khmelenko.lab.travisclient.adapter.RepoListAdapter;
import com.khmelenko.lab.travisclient.event.travis.FindReposEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.UserSuccessEvent;
import com.khmelenko.lab.travisclient.network.response.Repo;
import com.khmelenko.lab.travisclient.network.response.User;
import com.khmelenko.lab.travisclient.storage.AppSettings;
import com.khmelenko.lab.travisclient.task.TaskManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Main application activity
 *
 * @author Dmytro Khmelenko
 */
public class MainActivity extends AppCompatActivity {

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.main_repos_swipe_view)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.main_repos_recycler_view)
    RecyclerView mReposRecyclerView;

    private RepoListAdapter mRepoListAdapter;
    private List<Repo> mRepos = new ArrayList<>();

    private ProgressDialog mProgressDialog;
    private SearchView mSearchView;
    private TaskManager mTaskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mTaskManager = new TaskManager();

        mReposRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mReposRecyclerView.setLayoutManager(layoutManager);

        mRepoListAdapter = new RepoListAdapter(this, mRepos, new OnListItemListener() {
            @Override
            public void onItemSelected(int position) {
                Repo repo = mRepos.get(position);
                Intent intent = new Intent(MainActivity.this, RepoDetailsActivity.class);
                intent.putExtra(RepoDetailsActivity.REPO_SLUG_KEY, repo.getSlug());
                startActivity(intent);
            }
        });
        mReposRecyclerView.setAdapter(mRepoListAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_progress);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRepos();
            }
        });

        initToolbar();
        setupDrawerLayout();

        mProgressDialog = ProgressDialog.show(this, "", getString(R.string.loading_msg));
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
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.drawer_login:
                        Intent loginIntent = new Intent(MainActivity.this, AuthActivity.class);
                        startActivity(loginIntent);
                        break;
                    case R.id.drawer_logout:
                        AppSettings.putAccessToken("");
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
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressDialog.dismiss();

        mRepos.clear();
        mRepos.addAll(event.getRepos());
        mRepoListAdapter.notifyDataSetChanged();
    }

    /**
     * Raised on failed loading data
     *
     * @param event Event data
     */
    public void onEvent(LoadingFailedEvent event) {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressDialog.dismiss();

        String error = event.getTaskError().getMessage();
        String msg = getString(R.string.error_failed_loading_repos, error);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Raised on loaded user information
     *
     * @param event Event data
     */
    public void onEvent(UserSuccessEvent event) {
        User user = event.getUser();

        // TODO Cache user data

        String loginName = user.getLogin();
        mTaskManager.userRepos(loginName);
    }

}
