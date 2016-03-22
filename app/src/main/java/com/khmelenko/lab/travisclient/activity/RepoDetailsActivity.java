package com.khmelenko.lab.travisclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.fragment.BranchesFragment;
import com.khmelenko.lab.travisclient.fragment.BuildHistoryFragment;
import com.khmelenko.lab.travisclient.fragment.PullRequestsFragment;

import butterknife.ButterKnife;

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

    private String mRepoSlug;
    private boolean mReloadRequired;

    private PagerAdapter mAdapterViewPager;

    /**
     * Custom adapter for view pager
     */
    private class PagerAdapter extends FragmentPagerAdapter {
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
                    return BuildHistoryFragment.newInstance(mRepoSlug);
                case INDEX_BRANCHES:
                    return BranchesFragment.newInstance(mRepoSlug);
                case INDEX_PULL_REQUESTS:
                    return PullRequestsFragment.newInstance(mRepoSlug);
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

        @Override
        public int getItemPosition(Object object) {
            return mReloadRequired ? POSITION_NONE : POSITION_UNCHANGED;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_details);
        ButterKnife.bind(this);

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
    public void onBranchSelected(long buildId) {
        goToBuildDetails(buildId);
    }

    @Override
    public void onPullRequestSelected(long buildId) {
        goToBuildDetails(buildId);
    }

    /**
     * Navigates to the build details
     *
     * @param buildId Build ID
     */
    private void goToBuildDetails(long buildId) {
        mReloadRequired = false;

        Intent intent = new Intent(this, BuildDetailsActivity.class);
        intent.putExtra(BuildDetailsActivity.EXTRA_BUILD_ID, buildId);
        intent.putExtra(BuildDetailsActivity.EXTRA_REPO_SLUG, mRepoSlug);
        startActivityForResult(intent, BUILD_DETAILS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == BUILD_DETAILS_REQUEST_CODE) {
                boolean buildStateChanged = data.getBooleanExtra(BuildDetailsActivity.BUILD_STATE_CHANGED, false);
                if (buildStateChanged) {
                    mReloadRequired = true;
                    mAdapterViewPager.notifyDataSetChanged();
                }
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
}
