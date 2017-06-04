package com.khmelenko.lab.varis.presenter;

import android.text.TextUtils;

import com.khmelenko.lab.varis.common.Constants;
import com.khmelenko.lab.varis.mvp.MvpPresenter;
import com.khmelenko.lab.varis.network.response.Repo;
import com.khmelenko.lab.varis.network.response.User;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.storage.AppSettings;
import com.khmelenko.lab.varis.storage.CacheStorage;
import com.khmelenko.lab.varis.view.RepositoriesView;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Repositories presenter
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class RepositoriesPresenter extends MvpPresenter<RepositoriesView> {

    private final TravisRestClient mTravisRestClient;
    private final CacheStorage mCache;

    private User mUser;

    private final CompositeDisposable mSubscriptions;

    @Inject
    public RepositoriesPresenter(TravisRestClient restClient, CacheStorage storage) {
        mTravisRestClient = restClient;
        mCache = storage;

        mSubscriptions = new CompositeDisposable();
    }

    @Override
    public void onAttach() {
        mUser = mCache.restoreUser();
        getView().updateMenuState(AppSettings.getAccessToken());
        getView().updateUserData(mUser);
        getView().showProgress();

        reloadRepos();
    }

    @Override
    public void onDetach() {
        getView().hideProgress();
        mSubscriptions.clear();
    }

    /**
     * Starts loading repositories
     */
    public void reloadRepos() {
        String accessToken = AppSettings.getAccessToken();
        if (TextUtils.isEmpty(accessToken)) {
            Disposable subscription = mTravisRestClient.getApiService()
                    .getRepos("")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(reposHandler());

            mSubscriptions.add(subscription);
        } else {
            Disposable subscription = mTravisRestClient.getApiService()
                    .getUser()
                    .doOnSuccess(this::cacheUserData)
                    .flatMap(new Function<User, SingleSource<List<Repo>>>() {
                        @Override
                        public SingleSource<List<Repo>> apply(@NonNull User user) throws Exception {
                            String loginName = mUser.getLogin();
                            return mTravisRestClient.getApiService().getUserRepos(loginName);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(reposHandler());
            mSubscriptions.add(subscription);
        }
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

    private BiConsumer<List<Repo>, Throwable> reposHandler() {
        return (repos, throwable) -> {
            if (mUser != null) {
                getView().updateUserData(mUser);
            }

            if (throwable == null) {
                handleReposLoaded(repos);
            } else {
                handleLoadingFailed(throwable);
            }
        };
    }

    private void cacheUserData(User user) {
        mUser = user;

        // cache user data
        mCache.saveUser(mUser);
    }

    private void handleReposLoaded(List<Repo> repos) {
        getView().hideProgress();
        getView().setRepos(repos);

        mCache.saveRepos(repos);
    }

    private void handleLoadingFailed(Throwable throwable) {
        getView().hideProgress();
        getView().showError(throwable.getMessage());
    }
}
