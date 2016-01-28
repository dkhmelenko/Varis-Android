package com.khmelenko.lab.travisclient.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.TravisApp;
import com.khmelenko.lab.travisclient.event.travis.FindReposEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.fragment.ReposFragment;
import com.khmelenko.lab.travisclient.network.response.Repo;
import com.khmelenko.lab.travisclient.task.TaskManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Provides activity with search results
 *
 * @author Dmytro Khmelenko
 */
public final class SearchResultsActivity extends BaseActivity implements ReposFragment.ReposFragmentListener {

    @Inject
    EventBus mEventBus;

    private ReposFragment mFragment;

    private String mSearchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        TravisApp.instance().activityInjector().inject(this);
        ButterKnife.bind(this);

        mFragment = (ReposFragment) getFragmentManager().findFragmentById(R.id.search_fragment_repos);

        initToolbar();

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEventBus.unregister(this);
        mFragment.setLoadingProgress(false);
    }

    /**
     * Handles received intent
     *
     * @param intent Intent
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchQuery = query;
            mFragment.setLoadingProgress(true);
            startRepoSearch(query);
        }
    }

    /**
     * Starts repository search
     *
     * @param query Query string for search
     */
    private void startRepoSearch(String query) {
        TaskManager taskManager = new TaskManager();
        taskManager.findRepos(query);
    }

    /**
     * Initializes toolbar
     */
    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
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

    @Override
    public void onRepositorySelected(Repo repo) {
        Intent intent = new Intent(SearchResultsActivity.this, RepoDetailsActivity.class);
        intent.putExtra(RepoDetailsActivity.REPO_SLUG_KEY, repo.getSlug());
        startActivity(intent);
    }

    @Override
    public void onRefreshData() {
        startRepoSearch(mSearchQuery);
    }
}
