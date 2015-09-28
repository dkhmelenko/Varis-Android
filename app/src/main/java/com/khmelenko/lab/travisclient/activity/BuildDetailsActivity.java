package com.khmelenko.lab.travisclient.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.converter.BuildStateHelper;
import com.khmelenko.lab.travisclient.converter.TimeConverter;
import com.khmelenko.lab.travisclient.event.travis.BuildDetailsLoadedEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.fragment.JobsFragment;
import com.khmelenko.lab.travisclient.fragment.RawLogFragment;
import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.network.response.BuildDetails;
import com.khmelenko.lab.travisclient.network.response.Commit;
import com.khmelenko.lab.travisclient.network.response.Job;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.util.DateTimeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Build details
 *
 * @author Dmytro Khmelenko
 */
public class BuildDetailsActivity extends AppCompatActivity implements JobsFragment.JobsListener,
        RawLogFragment.OnRawLogFragmentListener {

    public static final String EXTRA_REPO_SLUG = "RepoSlug";
    public static final String EXTRA_BUILD_ID = "BuildId";

    @Bind(R.id.progressbarview)
    View mProgressBar;

    @Bind(R.id.build_details_build_data)
    View mBuildDetailsData;

    private String mRepoSlug;
    private long mBuildId;

    private TaskManager mTaskManager;
    private JobsFragment mJobsFragment;
    private RawLogFragment mRawLogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_details);
        ButterKnife.bind(this);
        initToolbar();

        mTaskManager = new TaskManager();

        mRepoSlug = getIntent().getStringExtra(EXTRA_REPO_SLUG);
        mBuildId = getIntent().getLongExtra(EXTRA_BUILD_ID, 0L);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

        mTaskManager.getBuildDetails(mRepoSlug, mBuildId);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mJobsFragment != null) {
            detachFragment(mJobsFragment);
        }
        if(mRawLogFragment != null) {
            detachFragment(mRawLogFragment);
        }
        super.onSaveInstanceState(outState);
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
        TextView number = (TextView) findViewById(R.id.item_build_number);
        TextView state = (TextView) findViewById(R.id.item_build_state);
        TextView branch = (TextView) findViewById(R.id.item_build_branch);
        TextView commitMessage = (TextView) findViewById(R.id.item_build_commit_message);
        TextView commitPerson = (TextView) findViewById(R.id.item_build_commit_person);
        TextView duration = (TextView) findViewById(R.id.item_build_duration);
        TextView finished = (TextView) findViewById(R.id.item_build_finished);

        Build build = details.getBuild();

        // build data
        number.setText(getString(R.string.build_build_number, build.getNumber()));
        String stateValue = build.getState();
        if (!TextUtils.isEmpty(stateValue)) {
            int buildColor = BuildStateHelper.getBuildColor(stateValue);
            state.setText(stateValue);
            state.setTextColor(buildColor);
            number.setTextColor(buildColor);

            Drawable drawable = BuildStateHelper.getBuildImage(stateValue);
            if (drawable != null) {
                drawable.setBounds(0, 0, 30, 30);
                number.setCompoundDrawables(drawable, null, null, null);
            }
        }

        // commit data
        Commit commit = details.getCommit();
        if (commit != null) {
            branch.setText(commit.getBranch());
            commitMessage.setText(commit.getMessage());
            commitPerson.setText(commit.getCommitterName());
        }

        // finished at
        if (!TextUtils.isEmpty(build.getFinishedAt())) {
            String formattedDate = DateTimeUtils.parseAndFormatDateTime(build.getFinishedAt());
            formattedDate = getString(R.string.build_finished_at, formattedDate);
            finished.setText(formattedDate);
        } else {
            String stateInProgress = getString(R.string.build_build_in_progress);
            String finishedValue = getString(R.string.build_finished_at, stateInProgress);
            finished.setText(finishedValue);
        }

        // duration
        if (build.getDuration() != 0) {
            String durationValue = TimeConverter.durationToString(build.getDuration());
            durationValue = getString(R.string.build_duration, durationValue);
            duration.setText(durationValue);
        } else {
            String stateInProgress = getString(R.string.build_build_in_progress);
            String durationValue = getString(R.string.build_duration, stateInProgress);
            duration.setText(durationValue);
        }
    }

    /**
     * Adds new fragment
     *
     * @param containerViewId ID of the container view for fragment
     * @param fragment        Fragment instance
     * @param fragmentTag     Fragment tag
     */
    protected void addFragment(@IdRes int containerViewId,
                               @NonNull Fragment fragment,
                               @NonNull String fragmentTag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(containerViewId, fragment, fragmentTag)
                .disallowAddToBackStack()
                .commit();
    }

    /**
     * Replaces fragment
     *
     * @param containerViewId    ID of the container view for fragment
     * @param fragment           Fragment instance
     * @param fragmentTag        Fragment tag
     * @param backStackStateName Name in back stack
     */
    protected void replaceFragment(@IdRes int containerViewId,
                                   @NonNull Fragment fragment,
                                   @NonNull String fragmentTag,
                                   @Nullable String backStackStateName) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .addToBackStack(backStackStateName)
                .commit();
    }

    /**
     * Detaches fragment
     *
     * @param fragment Fragment
     */
    protected void detachFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .detach(fragment)
                .commit();
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

        if (details.getJobs().size() > 1) {
            if(mJobsFragment == null) {
                mJobsFragment = JobsFragment.newInstance();
            }
            mJobsFragment.setJobs(details.getJobs());
            addFragment(R.id.build_details_jobs_container, mJobsFragment, "JobsFragment");
        } else if(details.getJobs().size() == 1){
            Job job = details.getJobs().get(0);
            if(mRawLogFragment == null) {
                mRawLogFragment = RawLogFragment.newInstance(job.getId());
            }
            addFragment(R.id.build_details_jobs_container, mRawLogFragment, "RawLogFragment");
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

        // TODO Show error
    }

    @Override
    public void onJobSelected(String job) {
        // TODO
    }

    @Override
    public void onLogLoaded() {
        // TODO
    }
}
