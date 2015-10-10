package com.khmelenko.lab.travisclient.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.adapter.OnListItemListener;
import com.khmelenko.lab.travisclient.adapter.RepoListAdapter;
import com.khmelenko.lab.travisclient.event.travis.FindReposEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.network.response.Repo;
import com.khmelenko.lab.travisclient.task.TaskManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Provides activity with search results
 *
 * @author Dmytro Khmelenko
 */
public class SearchResultsActivity extends AppCompatActivity {

    @Bind(R.id.search_repos_recycler_view)
    RecyclerView mReposRecyclerView;

    private RepoListAdapter mRepoListAdapter;
    private List<Repo> mRepos = new ArrayList<>();

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.bind(this);

        mReposRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mReposRecyclerView.setLayoutManager(layoutManager);

        mRepoListAdapter = new RepoListAdapter(this, mRepos, new OnListItemListener() {
            @Override
            public void onItemSelected(int position) {
                Repo repo = mRepos.get(position);
                Intent intent = new Intent(SearchResultsActivity.this, RepoDetailsActivity.class);
                intent.putExtra(RepoDetailsActivity.REPO_SLUG_KEY, repo.getSlug());
                startActivity(intent);
            }
        });
        mReposRecyclerView.setAdapter(mRepoListAdapter);

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
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Handles received intent
     *
     * @param intent Intent
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            startRepoSearch(query);
        }
    }

    /**
     * Starts repository search
     *
     * @param query Query string for search
     */
    private void startRepoSearch(String query) {
        mProgressDialog = ProgressDialog.show(this, "", getString(R.string.loading_msg));
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
     * Checks whether data existing or not
     */
    private void checkIfEmpty() {
        TextView emptyText = (TextView) findViewById(R.id.empty_text);
        emptyText.setText(R.string.repo_empty_text);
        if(mRepos.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    /**
     * Raised on loaded repositories
     *
     * @param event Event data
     */
    public void onEvent(FindReposEvent event) {
        mProgressDialog.dismiss();

        mRepos.clear();
        mRepos.addAll(event.getRepos());
        mRepoListAdapter.notifyDataSetChanged();

        checkIfEmpty();
    }

    /**
     * Raised on failed loading data
     *
     * @param event Event data
     */
    public void onEvent(LoadingFailedEvent event) {
        mProgressDialog.dismiss();

        checkIfEmpty();

        String msg = getString(R.string.error_failed_loading_repos, event.getTaskError().getMessage());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
