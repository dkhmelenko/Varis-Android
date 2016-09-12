package com.khmelenko.lab.travisclient.presenter;

import com.khmelenko.lab.travisclient.event.travis.FindReposEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.mvp.MvpPresenter;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.view.SearchResultsView;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Search results presenter
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public final class SearchResultsPresenter extends MvpPresenter<SearchResultsView> {

    private final TaskManager mTaskManager;
    private final EventBus mEventBus;

    @Inject
    public SearchResultsPresenter(TaskManager taskManager, EventBus eventBus) {
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
        getView().hideProgress();
    }

    /**
     * Raised on loaded repositories
     *
     * @param event Event data
     */
    public void onEvent(FindReposEvent event) {
        getView().hideProgress();
        getView().setSearchResults(event.getRepos());
    }

    /**
     * Raised on failed loading data
     *
     * @param event Event data
     */
    public void onEvent(LoadingFailedEvent event) {
        getView().hideProgress();
        getView().showLoadingError(event.getTaskError().getMessage());
    }

    /**
     * Starts repository search
     *
     * @param query Query string for search
     */
    public void startRepoSearch(String query) {
        mTaskManager.findRepos(query);
    }

}
