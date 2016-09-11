package com.khmelenko.lab.travisclient.presenter;

import com.khmelenko.lab.travisclient.event.travis.BranchesFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.BranchesLoadedEvent;
import com.khmelenko.lab.travisclient.event.travis.BuildHistoryFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.BuildHistoryLoadedEvent;
import com.khmelenko.lab.travisclient.event.travis.RequestsFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.RequestsLoadedEvent;
import com.khmelenko.lab.travisclient.mvp.MvpPresenter;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.view.RepoDetailsView;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Repository details presenter
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public final class RepoDetailsPresenter extends MvpPresenter<RepoDetailsView> {

    private final TaskManager mTaskManager;
    private final EventBus mEventBus;

    private String mRepoSlug;

    @Inject
    public RepoDetailsPresenter(TaskManager taskManager, EventBus eventBus) {
        mTaskManager = taskManager;
        mEventBus = eventBus;
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
     * Raised on loaded repositories
     *
     * @param event Event data
     */
    public void onEvent(BuildHistoryLoadedEvent event) {
        getView().updateBuildHistory(event.getBuildHistory());
    }

    /**
     * Raised on loaded repositories
     *
     * @param event Event data
     */
    public void onEvent(BranchesLoadedEvent event) {
        getView().updateBranches(event.getBranches());
    }

    /**
     * Raised on loaded requests
     *
     * @param event Event data
     */
    public void onEvent(RequestsLoadedEvent event) {
        getView().updatePullRequests(event.getRequests());
    }

    /**
     * Raised on failed loading build history
     *
     * @param event Event data
     */
    public void onEvent(BuildHistoryFailedEvent event) {
        getView().showBuildHistoryLoadingError(event.getTaskError().getMessage());
    }

    /**
     * Raised on failed loading branches
     *
     * @param event Event data
     */
    public void onEvent(BranchesFailedEvent event) {
        getView().showBranchesLoadingError(event.getTaskError().getMessage());
    }

    /**
     * Raised on failed loading requests
     *
     * @param event Event data
     */
    public void onEvent(RequestsFailedEvent event) {
        getView().showPullRequestsLoadingError(event.getTaskError().getMessage());
    }

    /**
     * Starts loading build history
     */
    public void loadBuildsHistory() {
        mTaskManager.getBuildHistory(mRepoSlug);
    }

    /**
     * Starts loading branches
     */
    public void loadBranches() {
        mTaskManager.getBranches(mRepoSlug);
    }

    /**
     * Starts loading requests
     */
    public void loadRequests() {
        mTaskManager.getRequests(mRepoSlug);
    }

    /**
     * Sets repository slug
     *
     * @param repoSlug Repository slug
     */
    public void setRepoSlug(String repoSlug) {
        mRepoSlug = repoSlug;
    }

    /**
     * Gets repository slug
     *
     * @return Repository slug
     */
    public String getRepoSlug() {
        return mRepoSlug;
    }

    /**
     * Loads repository details data
     */
    public void loadData() {
        loadBuildsHistory();
        loadBranches();
        loadRequests();
    }
}
