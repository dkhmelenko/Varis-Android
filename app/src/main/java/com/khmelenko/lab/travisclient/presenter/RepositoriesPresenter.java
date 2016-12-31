package com.khmelenko.lab.travisclient.presenter;

import android.text.TextUtils;

import com.khmelenko.lab.travisclient.common.Constants;
import com.khmelenko.lab.travisclient.event.travis.FindReposEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.UserSuccessEvent;
import com.khmelenko.lab.travisclient.mvp.MvpPresenter;
import com.khmelenko.lab.travisclient.network.response.User;
import com.khmelenko.lab.travisclient.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.travisclient.storage.AppSettings;
import com.khmelenko.lab.travisclient.storage.CacheStorage;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.view.RepositoriesView;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Repositories presenter
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class RepositoriesPresenter extends MvpPresenter<RepositoriesView> {

    private final TravisRestClient mTravisRestClient;
    private final EventBus mEventBus;
    private final TaskManager mTaskManager;
    private final CacheStorage mCache;

    private User mUser;

    @Inject
    public RepositoriesPresenter(TravisRestClient restClient, EventBus eventBus,
                                 TaskManager taskManager, CacheStorage storage) {
        mTravisRestClient = restClient;
        mEventBus = eventBus;
        mTaskManager = taskManager;
        mCache = storage;
    }

    @Override
    public void onAttach() {
        mEventBus.register(this);

        mUser = mCache.restoreUser();
        getView().updateMenuState(AppSettings.getAccessToken());
        getView().updateUserData(mUser);
        getView().showProgress();

        reloadRepos();
    }

    @Override
    public void onDetach() {
        mEventBus.unregister(this);
        getView().hideProgress();
    }

    /**
     * Starts loading repositories
     */
    public void reloadRepos() {
        String accessToken = AppSettings.getAccessToken();
        if (TextUtils.isEmpty(accessToken)) {
            mTaskManager.findRepos(null);
        } else {
            mTaskManager.getUser();
        }
    }

    /**
     * Raised on loaded repositories
     *
     * @param event Event data
     */
    public void onEvent(FindReposEvent event) {
        getView().hideProgress();
        getView().setRepos(event.getRepos());

        mCache.saveRepos(event.getRepos());
    }

    /**
     * Raised on failed loading data
     *
     * @param event Event data
     */
    public void onEvent(LoadingFailedEvent event) {
        getView().hideProgress();
        getView().showError(event.getTaskError().getMessage());
    }

    /**
     * Raised on loaded user information
     *
     * @param event Event data
     */
    public void onEvent(UserSuccessEvent event) {
        mUser = event.getUser();

        // cache user data
        mCache.saveUser(mUser);
        getView().updateUserData(mUser);

        String loginName = mUser.getLogin();
        mTaskManager.userRepos(loginName);
    }

    /**
     * Does user logout
     */
    public void userLogout() {
        mCache.deleteUser();
        mCache.deleteRepos();
        AppSettings.putAccessToken("");

        // reset back to open source url
        AppSettings.putServerUrl(Constants.OPEN_SOURCE_TRAVIS_URL);
        mTravisRestClient.updateTravisEndpoint(AppSettings.getServerUrl());
    }
}
