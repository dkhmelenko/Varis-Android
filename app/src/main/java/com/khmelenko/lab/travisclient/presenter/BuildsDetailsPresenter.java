package com.khmelenko.lab.travisclient.presenter;

import android.text.TextUtils;

import com.khmelenko.lab.travisclient.event.travis.BuildDetailsLoadedEvent;
import com.khmelenko.lab.travisclient.event.travis.CancelBuildFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.CancelBuildSuccessEvent;
import com.khmelenko.lab.travisclient.event.travis.IntentUrlSuccessEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.LogFailEvent;
import com.khmelenko.lab.travisclient.event.travis.LogLoadedEvent;
import com.khmelenko.lab.travisclient.event.travis.RestartBuildFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.RestartBuildSuccessEvent;
import com.khmelenko.lab.travisclient.mvp.MvpPresenter;
import com.khmelenko.lab.travisclient.network.response.BuildDetails;
import com.khmelenko.lab.travisclient.network.response.Job;
import com.khmelenko.lab.travisclient.storage.AppSettings;
import com.khmelenko.lab.travisclient.storage.CacheStorage;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.view.BuildDetailsView;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Presenter for BuildDetails
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class BuildsDetailsPresenter extends MvpPresenter<BuildDetailsView> {

    public static final int LOAD_LOG_MAX_ATTEMPT = 3;

    public final EventBus mEventBus;
    public final TaskManager mTaskManager;
    public final CacheStorage mCache;

    private String mRepoSlug;
    private long mBuildId;
    private long mJobId;

    private int mLoadLogAttempt = 0;

    @Inject
    public BuildsDetailsPresenter(EventBus eventBus, TaskManager taskManager, CacheStorage cache) {
        mEventBus = eventBus;
        mTaskManager = taskManager;
        mCache = cache;
    }

    @Override
    public void onAttach() {
        mEventBus.register(this);
    }

    @Override
    public void onDetach() {
        mEventBus.unregister(this);
    }

    /**
     * Starts loading log file
     *
     * @param jobId Job ID
     */
    public void startLoadingLog(long jobId) {
        mJobId = jobId;
        String accessToken = AppSettings.getAccessToken();
        if (TextUtils.isEmpty(accessToken)) {
            mTaskManager.getLogUrl(jobId);
        } else {
            String auth = String.format("token %1$s", AppSettings.getAccessToken());
            mTaskManager.getLogUrl(auth, jobId);
        }
    }

    /**
     * Starts loading data
     *
     * @param intentUrl Intent URL
     * @param repoSlug  Repository slug
     * @param buildId   Build ID
     */
    public void startLoadingData(String intentUrl, String repoSlug, long buildId) {
        mRepoSlug = repoSlug;
        mBuildId = buildId;

        if (!TextUtils.isEmpty(intentUrl)) {
            mTaskManager.intentUrl(intentUrl);
        } else {
            mTaskManager.getBuildDetails(mRepoSlug, mBuildId);
        }

        getView().showProgress();
    }

    /**
     * Raised on failed loading data
     *
     * @param event Event data
     */
    public void onEvent(LoadingFailedEvent event) {
        getView().hideProgress();
        getView().updateBuildDetails(null);
        getView().showLoadingError(event.getTaskError().getMessage());
    }

    /**
     * Raised on failed loading log data
     *
     * @param event Event data
     */
    public void onEvent(LogFailEvent event) {
        if(mLoadLogAttempt >= LOAD_LOG_MAX_ATTEMPT) {
            getView().showLogError();
            getView().showLoadingError(event.getTaskError().getMessage());
        } else {
            mLoadLogAttempt++;
            startLoadingLog(mJobId);
        }
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
     * Raised on failed build restart
     *
     * @param event Event data
     */
    public void onEvent(RestartBuildFailedEvent event) {
        getView().showLoadingError(event.getTaskError().getMessage());

        // reload build details
        mTaskManager.getBuildDetails(mRepoSlug, mBuildId);
    }

    /**
     * Raised on success build cancel
     *
     * @param event Event data
     */
    public void onEvent(CancelBuildSuccessEvent event) {
        // reload build details
        mTaskManager.getBuildDetails(mRepoSlug, mBuildId);
    }

    /**
     * Raised on failed build cancel
     *
     * @param event Event data
     */
    public void onEvent(CancelBuildFailedEvent event) {
        getView().showLoadingError(event.getTaskError().getMessage());

        // reload build details
        mTaskManager.getBuildDetails(mRepoSlug, mBuildId);
    }

    /**
     * Raised on loaded url for the log file
     *
     * @param event Event data
     */
    public void onEvent(LogLoadedEvent event) {
        getView().setLogUrl(event.getLogUrl());
    }

    /**
     * Raised on finished intent URL
     *
     * @param event Event data
     */
    public void onEvent(IntentUrlSuccessEvent event) {

        parseIntentUrl(event.getRedirectUrl());
        boolean isError = TextUtils.isEmpty(mRepoSlug) || mBuildId == 0;

        if (isError) {
            // handle error
            TaskError taskError = new TaskError(TaskError.NETWORK_ERROR, "");
            onEvent(new LoadingFailedEvent(taskError));
        } else {
            mTaskManager.getBuildDetails(mRepoSlug, mBuildId);
        }
    }

    /**
     * Raised on loaded build details
     *
     * @param event Event data
     */
    public void onEvent(BuildDetailsLoadedEvent event) {
        getView().hideProgress();
        getView().updateBuildDetails(event.getBuildDetails());

        BuildDetails details = event.getBuildDetails();

        if (details != null) {
            if (details.getJobs().size() > 1) {
                getView().showBuildJobs(details.getJobs());
            } else if (details.getJobs().size() == 1) {
                getView().showBuildLogs();

                Job job = details.getJobs().get(0);
                startLoadingLog(job.getId());
            }

            // if user logged in, show additional actions for the repo
            String appToken = AppSettings.getAccessToken();
            if (!TextUtils.isEmpty(appToken)) {
                getView().showAdditionalActionsForBuild(details);
            }
        }
    }

    /**
     * Parses intent URL
     *
     * @param intentUrl Intent URL
     */
    private void parseIntentUrl(String intentUrl) {
        final int ownerIndex = 1;
        final int repoNameIndex = 2;
        final int buildIdIndex = 4;
        final int pathLength = 5;

        try {
            URL url = new URL(intentUrl);
            String path = url.getPath();
            String[] items = path.split("/");
            if (items.length >= pathLength) {
                mRepoSlug = String.format("%s/%s", items[ownerIndex], items[repoNameIndex]);
                mBuildId = Long.valueOf(items[buildIdIndex]);
            }
        } catch (MalformedURLException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restarts build process
     */
    public void restartBuild() {
        mTaskManager.restartBuild(mBuildId);
    }

    /**
     * Cancels build process
     */
    public void cancelBuild() {
        mTaskManager.cancelBuild(mBuildId);
    }

    /**
     * Defines whether the user can contribute to the repository or not
     *
     * @return True if user can contribute to the repository. False otherwise
     */
    public boolean canUserContributeToRepo() {
        boolean canContributeToRepo = false;
        String[] userRepos = mCache.restoreRepos();
        for (String repo : userRepos) {
            if (repo.equals(mRepoSlug)) {
                canContributeToRepo = true;
                break;
            }
        }
        return canContributeToRepo;
    }
}
