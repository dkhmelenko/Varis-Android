package com.khmelenko.lab.travisclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.TravisApp;
import com.khmelenko.lab.travisclient.adapter.SmartFragmentStatePagerAdapter;
import com.khmelenko.lab.travisclient.event.travis.BranchesFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.BranchesLoadedEvent;
import com.khmelenko.lab.travisclient.event.travis.BuildHistoryFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.BuildHistoryLoadedEvent;
import com.khmelenko.lab.travisclient.event.travis.RequestsFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.RequestsLoadedEvent;
import com.khmelenko.lab.travisclient.fragment.BranchesFragment;
import com.khmelenko.lab.travisclient.fragment.BuildHistoryFragment;
import com.khmelenko.lab.travisclient.fragment.PullRequestsFragment;
import com.khmelenko.lab.travisclient.task.TaskManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Repository Details Activity
 *
 * @author Dmytro Khmelenko
 */
public final class RepoDetailsActivity extends BaseActivity implements BuildHistoryFragment.BuildHistoryListener,
        BranchesFragment.BranchesListener, PullRequestsFragment.PullRequestsListener {

    private static final int BUILD_DETAILS_REQUEST_CODE = 0;

    public static final String REPO_SLUG_KEY = "RepoSlug";
    public static final String RELOAD_REQUIRED_KEY = "ReloadRequiredKey";

    @Inject
    EventBus mEventBus;
    @Inject
    TaskManager mTaskManager;

    private String mRepoSlug;
    private boolean mReloadRequired;
    private boolean mInitialLoad = true;

    private SmartFragmentStatePagerAdapter mAdapterViewPager;

    /**
     * Custom adapter for view pager
     */
    private class PagerAdapter extends SmartFragmentStatePagerAdapter {
        private static final int ITEMS_COUNT = 3;
        private static final int INDEX_BUILD_HISTORY = 0;
        private static final int INDEX_BRANCHES = 1;
        private static final int INDEX_PULL_REQUESTS = 2;

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return ITEMS_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case INDEX_BUILD_HISTORY:
                    return BuildHistoryFragment.newInstance();
                case INDEX_BRANCHES:
                    return BranchesFragment.newInstance();
                case INDEX_PULL_REQUESTS:
                    return PullRequestsFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case INDEX_BUILD_HISTORY:
                    return getString(R.string.repo_details_tab_build_history);
                case INDEX_BRANCHES:
                    return getString(R.string.repo_details_tab_branches);
                case INDEX_PULL_REQUESTS:
                    return getString(R.string.repo_details_tab_pull_requests);
                default:
                    return null;
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_details);
        ButterKnife.bind(this);
        TravisApp.instance().activityInjector().inject(this);

        mRepoSlug = getIntent().getStringExtra(REPO_SLUG_KEY);

        initToolbar();

        String projectName = mRepoSlug.substring(mRepoSlug.indexOf("/") + 1);
        setTitle(projectName);

        // setting view pager
        ViewPager vpPager = (ViewPager) findViewById(R.id.repo_details_view_pager);
        mAdapterViewPager = new PagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(mAdapterViewPager);
        vpPager.setOffscreenPageLimit(PagerAdapter.ITEMS_COUNT);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.repo_details_view_tabs);
        tabLayout.setupWithViewPager(vpPager);
    }

    @Override
    public void onResume() {
        super.onResume();
        mEventBus.register(this);

        if (mInitialLoad || mReloadRequired) {
            mInitialLoad = false;

            loadBuildsHistory();
            loadBranches();
            loadRequests();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mEventBus.unregister(this);
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

    @Override
    public void onBuildSelected(long buildId) {
        goToBuildDetails(buildId);
    }

    @Override
    public void onReloadBuildHistory() {
        loadBuildsHistory();
    }

    @Override
    public void onBranchSelected(long buildId) {
        goToBuildDetails(buildId);
    }

    @Override
    public void onReloadBranches() {
        loadBranches();
    }

    @Override
    public void onPullRequestSelected(long buildId) {
        goToBuildDetails(buildId);
    }

    @Override
    public void onReloadPullRequests() {
        loadRequests();
    }

    /**
     * Navigates to the build details
     *
     * @param buildId Build ID
     */
    private void goToBuildDetails(long buildId) {
        Intent intent = new Intent(this, BuildDetailsActivity.class);
        intent.putExtra(BuildDetailsActivity.EXTRA_BUILD_ID, buildId);
        intent.putExtra(BuildDetailsActivity.EXTRA_REPO_SLUG, mRepoSlug);
        startActivityForResult(intent, BUILD_DETAILS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == BUILD_DETAILS_REQUEST_CODE) {
                mReloadRequired |= data.getBooleanExtra(BuildDetailsActivity.BUILD_STATE_CHANGED, false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(RELOAD_REQUIRED_KEY, mReloadRequired);
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }

    /**
     * Raised on loaded repositories
     *
     * @param event Event data
     */
    public void onEvent(BuildHistoryLoadedEvent event) {
        BuildHistoryFragment fragment =
                (BuildHistoryFragment) mAdapterViewPager.getRegisteredFragment(PagerAdapter.INDEX_BUILD_HISTORY);
        fragment.setBuildHistory(event.getBuildHistory());
    }

    /**
     * Raised on loaded repositories
     *
     * @param event Event data
     */
    public void onEvent(BranchesLoadedEvent event) {
        BranchesFragment fragment =
                (BranchesFragment) mAdapterViewPager.getRegisteredFragment(PagerAdapter.INDEX_BRANCHES);
        fragment.setBranches(event.getBranches());
    }

    /**
     * Raised on loaded requests
     *
     * @param event Event data
     */
    public void onEvent(RequestsLoadedEvent event) {
        PullRequestsFragment fragment =
                (PullRequestsFragment) mAdapterViewPager.getRegisteredFragment(PagerAdapter.INDEX_PULL_REQUESTS);
        fragment.setPullRequests(event.getRequests());
    }

    /**
     * Raised on failed loading build history
     *
     * @param event Event data
     */
    public void onEvent(BuildHistoryFailedEvent event) {
        BuildHistoryFragment fragment =
                (BuildHistoryFragment) mAdapterViewPager.getRegisteredFragment(PagerAdapter.INDEX_BUILD_HISTORY);
        fragment.setBuildHistory(null);

        String msg = getString(R.string.error_failed_loading_build_history, event.getTaskError().getMessage());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Raised on failed loading branches
     *
     * @param event Event data
     */
    public void onEvent(BranchesFailedEvent event) {
        BranchesFragment fragment =
                (BranchesFragment) mAdapterViewPager.getRegisteredFragment(PagerAdapter.INDEX_BRANCHES);
        fragment.setBranches(null);

        String msg = getString(R.string.error_failed_loading_branches, event.getTaskError().getMessage());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Raised on failed loading requests
     *
     * @param event Event data
     */
    public void onEvent(RequestsFailedEvent event) {
        PullRequestsFragment fragment =
                (PullRequestsFragment) mAdapterViewPager.getRegisteredFragment(PagerAdapter.INDEX_PULL_REQUESTS);
        fragment.setPullRequests(null);

        String msg = getString(R.string.error_failed_loading_pull_requests, event.getTaskError().getMessage());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Starts loading build history
     */
    private void loadBuildsHistory() {
        mTaskManager.getBuildHistory(mRepoSlug);
    }

    /**
     * Starts loading branches
     */
    private void loadBranches() {
        mTaskManager.getBranches(mRepoSlug);
    }

    /**
     * Starts loading requests
     */
    private void loadRequests() {
        mTaskManager.getRequests(mRepoSlug);
    }
}
