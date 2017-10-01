package com.khmelenko.lab.varis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.khmelenko.lab.varis.R;
import com.khmelenko.lab.varis.TravisApp;
import com.khmelenko.lab.varis.converter.BuildStateHelper;
import com.khmelenko.lab.varis.fragment.JobsFragment;
import com.khmelenko.lab.varis.fragment.RawLogFragment;
import com.khmelenko.lab.varis.log.LogEntryComponent;
import com.khmelenko.lab.varis.mvp.MvpActivity;
import com.khmelenko.lab.varis.network.response.Build;
import com.khmelenko.lab.varis.network.response.BuildDetails;
import com.khmelenko.lab.varis.network.response.Commit;
import com.khmelenko.lab.varis.network.response.Job;
import com.khmelenko.lab.varis.presenter.BuildsDetailsPresenter;
import com.khmelenko.lab.varis.view.BuildDetailsView;
import com.khmelenko.lab.varis.widget.BuildView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Build details
 *
 * @author Dmytro Khmelenko
 */
public final class BuildDetailsActivity extends MvpActivity<BuildsDetailsPresenter> implements
                                                                                    BuildDetailsView,
                                                                                    JobsFragment.JobsListener,
                                                                                    RawLogFragment.OnRawLogFragmentListener {

    public static final String EXTRA_REPO_SLUG = "RepoSlug";
    public static final String EXTRA_BUILD_ID = "BuildId";
    public static final String BUILD_STATE_CHANGED = "BuildsStateChanged";

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

    @Inject
    BuildsDetailsPresenter mPresenter;

    private JobsFragment mJobsFragment;
    private RawLogFragment mRawLogFragment;

    private boolean mCanContributeToRepo;
    private boolean mBuildInProgressState;
    private boolean mBuildStateChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_details);
        ButterKnife.bind(this);
        TravisApp.instance().activityInjector().inject(this);
        initToolbar();
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
                getPresenter().restartBuild();
                handleBuildAction();
                return true;
            case R.id.build_activity_action_cancel:
                getPresenter().cancelBuild();
                handleBuildAction();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onJobSelected(Job job) {
        if (mRawLogFragment == null) {
            mRawLogFragment = RawLogFragment.newInstance();
        }
        replaceFragment(R.id.build_details_container, mRawLogFragment, RAW_LOG_FRAGMENT_TAG, null);

        getPresenter().startLoadingLog(job.getId());
    }

    @Override
    public void onLogLoaded() {
        mScrollBtn.show();
    }

    @OnClick(R.id.build_details_scroll_btn)
    public void scrollContent() {
        mScrollBtn.hide();
        mScrollUpBtn.show();

        mBuildDetailsLayout.post(() -> mBuildDetailsLayout.fullScroll(View.FOCUS_DOWN));
    }

    @OnClick(R.id.build_details_scroll_up_btn)
    public void setScrollUpContent() {
        mScrollBtn.show();
        mScrollUpBtn.hide();

        mBuildDetailsLayout.post(() -> mBuildDetailsLayout.fullScroll(View.FOCUS_UP));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(BUILD_STATE_CHANGED, mBuildStateChanged);
        setResult(RESULT_OK, intent);

        super.onBackPressed();
        mScrollBtn.hide();
        mScrollUpBtn.hide();
    }

    @Override
    protected BuildsDetailsPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void attachPresenter() {
        getPresenter().attach(this);

        final Intent intent = getIntent();
        final String action = intent.getAction();

        String intentUrl = null;
        String repoSlug = null;
        long buildId = 0L;
        if (Intent.ACTION_VIEW.equals(action)) {
            intentUrl = intent.getDataString();
        } else {
            repoSlug = getIntent().getStringExtra(EXTRA_REPO_SLUG);
            buildId = getIntent().getLongExtra(EXTRA_BUILD_ID, 0L);
        }

        getPresenter().startLoadingData(intentUrl, repoSlug, buildId);
    }

    /**
     * Shows additional actions for build
     *
     * @param details Build details
     */
    @Override
    public void showAdditionalActionsForBuild(BuildDetails details) {

        // check whether the user can contribute to this repo
        mCanContributeToRepo = getPresenter().canUserContributeToRepo();
        if (mCanContributeToRepo) {
            mBuildInProgressState = BuildStateHelper.isInProgress(details.getBuild().getState());
            invalidateOptionsMenu();
        }
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingError(String message) {
        String msg = getString(R.string.error_failed_loading_build_details, message);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateBuildDetails(BuildDetails buildDetails) {
        if (buildDetails != null) {
            mBuildDetailsData.setVisibility(View.VISIBLE);
            showBuildDetails(buildDetails);
        } else {
            mBuildDetailsData.setVisibility(View.GONE);
        }
        checkIfEmpty(buildDetails);
    }

    @Override
    public void showLogError() {
        mRawLogFragment.showProgress(false);
        mRawLogFragment.showError(true);
    }

    @Override
    public void setLog(LogEntryComponent log) {
        mRawLogFragment.showLog(log);
    }

    @Override
    public void showBuildJobs(List<Job> jobs) {
        if (mJobsFragment == null) {
            mJobsFragment = JobsFragment.newInstance();
        }
        mJobsFragment.setJobs(jobs);
        addFragment(R.id.build_details_container, mJobsFragment, JOBS_FRAGMENT_TAG);
    }

    @Override
    public void showBuildLogs() {
        if (mRawLogFragment == null) {
            mRawLogFragment = RawLogFragment.newInstance();
        }
        addFragment(R.id.build_details_container, mRawLogFragment, RAW_LOG_FRAGMENT_TAG);
    }

    /**
     * Handles build action
     */
    private void handleBuildAction() {
        showProgress();

        mCanContributeToRepo = false;
        invalidateOptionsMenu();

        mBuildStateChanged = true;
    }

    /**
     * Initializes toolbar
     */
    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
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

        BuildView buildView = findViewById(R.id.build_details_build_data);
        buildView.setState(build);
        buildView.setCommit(commit);
    }

    /**
     * Checks whether data existing or not
     */
    private void checkIfEmpty(BuildDetails details) {
        TextView emptyText = findViewById(R.id.empty_text);
        emptyText.setText(R.string.build_details_empty);
        if (details == null) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }
}
