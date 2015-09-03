package com.khmelenko.lab.travisclient.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.adapter.BuildListAdapter;
import com.khmelenko.lab.travisclient.event.travis.FindReposEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.RepoStatusLoadedEvent;
import com.khmelenko.lab.travisclient.network.response.RepoStatus;
import com.khmelenko.lab.travisclient.task.TaskManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Builds history
 *
 * @author Dmytro Khmelenko
 */
public class BuildHistoryActivity extends AppCompatActivity {

    public static final String REPO_SLUG_KEY = "RepoSlug";

    @Bind(R.id.builds_history_swipe_view)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.builds_history_recycler_view)
    RecyclerView mBuildHistoryRecyclerView;

    private BuildListAdapter mBuildListAdapter;
    private RepoStatus mRepoStatus;
    private String mRepoSlug;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_history);
        ButterKnife.bind(this);

        mRepoSlug = getIntent().getStringExtra(REPO_SLUG_KEY);

        mBuildHistoryRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBuildHistoryRecyclerView.setLayoutManager(layoutManager);

        mBuildListAdapter = new BuildListAdapter(this, mRepoStatus);
        mBuildHistoryRecyclerView.setAdapter(mBuildListAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_progress);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBuilds();
            }
        });

        initToolbar();

        mProgressDialog = ProgressDialog.show(this, "", getString(R.string.loading_msg));
        loadBuilds();
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
     * Starts loading build history
     */
    private void loadBuilds() {
        TaskManager taskManager = new TaskManager();
        taskManager.getRepoStatus(mRepoSlug);
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
    public void onEvent(RepoStatusLoadedEvent event) {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressDialog.dismiss();

        mRepoStatus = event.getRepoStatus();
        mBuildListAdapter.setRepoStatus(mRepoStatus);
        mBuildListAdapter.notifyDataSetChanged();
    }

    /**
     * Raised on failed loading data
     *
     * @param event Event data
     */
    public void onEvent(LoadingFailedEvent event) {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressDialog.dismiss();

        String msg = getString(R.string.error_failed_loading_build_history, event.getTaskError().getMessage());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
