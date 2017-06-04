package com.khmelenko.lab.varis.presenter;

import android.text.TextUtils;

import com.khmelenko.lab.varis.mvp.MvpPresenter;
import com.khmelenko.lab.varis.network.response.Repo;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.view.SearchResultsView;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Search results presenter
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class SearchResultsPresenter extends MvpPresenter<SearchResultsView> {

    private final TravisRestClient mTravisRestClient;

    private final CompositeDisposable mSubscriptions;

    @Inject
    public SearchResultsPresenter(TravisRestClient travisRestClient) {
        mTravisRestClient = travisRestClient;

        mSubscriptions = new CompositeDisposable();
    }

    @Override
    public void onAttach() {
        // do nothing
    }

    @Override
    public void onDetach() {
        getView().hideProgress();
        mSubscriptions.clear();
    }

    /**
     * Starts repository search
     *
     * @param query Query string for search
     */
    public void startRepoSearch(String query) {
        Single<List<Repo>> reposSingle;
        if (!TextUtils.isEmpty(query)) {
            reposSingle = mTravisRestClient.getApiService().getRepos(query);
        } else {
            reposSingle = mTravisRestClient.getApiService().getRepos();
        }
        Disposable subscription = reposSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((repos, throwable) -> {
                    getView().hideProgress();
                    if (throwable == null) {
                        getView().setSearchResults(repos);
                    } else {
                        getView().showLoadingError(throwable.getMessage());
                    }
                });
        mSubscriptions.add(subscription);
    }

}
