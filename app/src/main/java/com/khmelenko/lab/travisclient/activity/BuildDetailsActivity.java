package com.khmelenko.lab.travisclient.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.TravisApp;
import com.khmelenko.lab.travisclient.converter.BuildStateHelper;
import com.khmelenko.lab.travisclient.event.travis.BuildDetailsLoadedEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.LogFailEvent;
import com.khmelenko.lab.travisclient.event.travis.LogLoadedEvent;
import com.khmelenko.lab.travisclient.event.travis.RestartBuildSuccessEvent;
import com.khmelenko.lab.travisclient.fragment.JobsFragment;
import com.khmelenko.lab.travisclient.fragment.RawLogFragment;
import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.network.response.BuildDetails;
import com.khmelenko.lab.travisclient.network.response.Commit;
import com.khmelenko.lab.travisclient.network.response.Job;
import com.khmelenko.lab.travisclient.storage.AppSettings;
import com.khmelenko.lab.travisclient.storage.CacheStorage;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.view.BuildView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Build details
 *
 * @author Dmytro Khmelenko
 */
public final class BuildDetailsActivity extends BaseActivity implements JobsFragment.JobsListener,
        RawLogFragment.OnRawLogFragmentListener {

    public static final String EXTRA_REPO_SLUG = "RepoSlug";
    public static final String EXTRA_BUILD_ID = "BuildId";

    private static final String RAW_LOG_FRAGMENT_TAG = "RawLogFragment";
    private static final String JOBS_FRAGMENT_TAG = "JobsFragment";

    @Bind(R.id.progressbarview)
    View mProgressBar;

    @Bind(R.id.build_details_build_data)
    View mBuildDetailsData;

    @Bind(R.id.build_details_layout)
    ScrollView mBuildDetailsLayout;

    @Bind(R.id.build_details_scroll_btn)
    FloatingActionButton mScrollBtn;

    @Bind(R.id.build_details_scroll_up_btn)
    FloatingActionButton mScrollUpBtn;

    private String mRepoSlug;
    private long mBuildId;

    @Inject
    EventBus mEventBus;

    private TaskManager mTaskManager;
    private CacheStorage mCache;
    private JobsFragment mJobsFragment;
    private RawLogFragment mRawLogFragment;

    private boolean mCanContributeToRepo;
    private boolean mBuildInProgressState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_details);
        ButterKnife.bind(this);
        TravisApp.instance().activityInjector().inject(this);
        initToolbar();

        mTaskManager = new TaskManager();
        mCache = CacheStorage.newInstance();

        mRepoSlug = getIntent().getStringExtra(EXTRA_REPO_SLUG);
        mBuildId = getIntent().getLongExtra(EXTRA_BUILD_ID, 0L);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEventBus.register(this);

        mTaskManager.getBuildDetails(mRepoSlug, mBuildId);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mJobsFragment != null) {
            detachFragment(mJobsFragment);
        }
        if (mRawLogFragment != null) {
            detachFragment(mRawLogFragment);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_build_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemCancel = menu.findItem(R.id.build_activity_action_cancel);
        MenuItem itemRestart = menu.findItem(R.id.build_activity_action_restart);
        if (mCanContributeToRepo) {
            if (mBuildInProgressState) {
                itemCancel.setVisible(true);
            } else {
                itemRestart.setVisible(true);
            }
        } else {
            itemCancel.setVisible(false);
            itemRestart.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.build_activity_action_restart:
                mTaskManager.restartBuild(mBuildId);
                mProgressBar.setVisibility(View.VISIBLE);

                mCanContributeToRepo = false;
                invalidateOptionsMenu();
                return true;
            case R.id.build_activity_action_cancel:
                // TODO cancel build
                return true;
        }
        return super.onOptionsItemSelected(item);
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
     * Shows build details
     *
     * @param details Build details
     */
    private void showBuildDetails(BuildDetails details) {
        Build build = details.getBuild();
        Commit commit = details.getCommit();

        BuildView buildView = (BuildView) findViewById(R.id.build_details_build_data);
        buildView.setBuildData(build, commit);
    }

    /**
     * Checks whether data existing or not
     */
    private void checkIfEmpty(BuildDetails details) {
        TextView emptyText = (TextView) findViewById(R.id.empty_text);
        emptyText.setText(R.string.build_details_empty);
        if (details == null) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    /**
     * Raised on loaded build details
     *
     * @param event Event data
     */
    public void onEvent(BuildDetailsLoadedEvent event) {
        mProgressBar.setVisibility(View.GONE);
        mBuildDetailsData.setVisibility(View.VISIBLE);

        BuildDetails details = event.getBuildDetails();
        showBuildDetails(details);

        checkIfEmpty(details);

        if (details != null) {
            if (details.getJobs().size() > 1) {
                if (mJobsFragment == null) {
                    mJobsFragment = JobsFragment.newInstance();
                }
                mJobsFragment.setJobs(details.getJobs());
                addFragment(R.id.build_details_container, mJobsFragment, JOBS_FRAGMENT_TAG);
            } else if (details.getJobs().size() == 1) {
                Job job = details.getJobs().get(0);

                if (mRawLogFragment == null) {
                    mRawLogFragment = RawLogFragment.newInstance();
                }
                addFragment(R.id.build_details_container, mRawLogFragment, RAW_LOG_FRAGMENT_TAG);
                startLoadingLog(job.getId());
            }

            // if user logged in, show additional actions for the repo
            String appToken = AppSettings.getAccessToken();
            if (!TextUtils.isEmpty(appToken)) {
                showAdditionalActionsForBuild(details);
            }
        }
    }

    /**
     * Shows additional actions for build
     *
     * @param details Build details
     */
    private void showAdditionalActionsForBuild(BuildDetails details) {

        // check whether the user can contribute to this repo
        mCanContributeToRepo = false;
        String[] userRepos = mCache.restoreRepos();
        for (String repo : userRepos) {
            if (repo.equals(mRepoSlug)) {
                mCanContributeToRepo = true;
                break;
            }
        }

        if (mCanContributeToRepo) {
            mBuildInProgressState = BuildStateHelper.isInProgress(details.getBuild().getState());
            invalidateOptionsMenu();
        }
    }


    /**
     * Starts loading log file
     *
     * @param jobId Job ID
     */
    private void startLoadingLog(long jobId) {
        String accessToken = AppSettings.getAccessToken();
        if (TextUtils.isEmpty(accessToken)) {
            mTaskManager.getLogUrl(jobId);
        } else {
            String auth = String.format("token %1$s", AppSettings.getAccessToken());
            mTaskManager.getLogUrl(auth, jobId);
        }
    }

    /**
     * Raised on failed loading data
     *
     * @param event Event data
     */
    public void onEvent(LoadingFailedEvent event) {
        mProgressBar.setVisibility(View.GONE);
        mBuildDetailsData.setVisibility(View.GONE);

        checkIfEmpty(null);

        String msg = getString(R.string.error_failed_loading_build_details, event.getTaskError().getMessage());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Raised on failed loading log data
     *
     * @param event Event data
     */
    public void onEvent(LogFailEvent event) {
        mRawLogFragment.showProgress(false);
        mRawLogFragment.showError(true);

        String msg = getString(R.string.error_failed_loading_build_details, event.getTaskError().getMessage());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Raised on success build restart
     *
     * @param event Event data
     */
    public void onEvent(RestartBuildSuccessEvent event) {
        // reload build details
        mTaskManager.getBuildDetails(mRepoSlug, mBuildId);
    }

    /**
     * Raised on loaded url for the log file
     *
     * @param event Event data
     */
    public void onEvent(LogLoadedEvent event) {
        mRawLogFragment.loadUrl(event.getLogUrl());
    }

    @Override
    public void onJobSelected(Job job) {
        if (mRawLogFragment == null) {
            mRawLogFragment = RawLogFragment.newInstance();
        }
        replaceFragment(R.id.build_details_container, mRawLogFragment, RAW_LOG_FRAGMENT_TAG, null);

        startLoadingLog(job.getId());
    }

    @Override
    public void onLogLoaded() {
        mScrollBtn.show();
    }

    @OnClick(R.id.build_details_scroll_btn)
    public void scrollContent() {
        mScrollBtn.hide();
        mScrollUpBtn.show();

        mBuildDetailsLayout.post(new Runnable() {
            @Override
            public void run() {
                mBuildDetailsLayout.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @OnClick(R.id.build_details_scroll_up_btn)
    public void setScrollUpContent() {
        mScrollBtn.show();
        mScrollUpBtn.hide();

        mBuildDetailsLayout.post(new Runnable() {
            @Override
            public void run() {
                mBuildDetailsLayout.fullScroll(View.FOCUS_UP);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mScrollBtn.hide();
        mScrollUpBtn.hide();
    }
}
