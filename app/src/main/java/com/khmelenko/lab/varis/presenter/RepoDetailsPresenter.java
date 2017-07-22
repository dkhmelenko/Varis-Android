package com.khmelenko.lab.varis.presenter;

import com.khmelenko.lab.varis.mvp.MvpPresenter;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.view.RepoDetailsView;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Repository details presenter
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class RepoDetailsPresenter extends MvpPresenter<RepoDetailsView> {

    private final TravisRestClient mTravisRestClient;

    private String mRepoSlug;

    private CompositeDisposable mSubscriptions;

    @Inject
    public RepoDetailsPresenter(TravisRestClient travisRestClient) {
        mTravisRestClient = travisRestClient;

        mSubscriptions = new CompositeDisposable();
    }

    @Override
    public void onAttach() {
        // do nothing
    }

    @Override
    public void onDetach() {
        mSubscriptions.clear();
    }

    /**
     * Starts loading build history
     */
    public void loadBuildsHistory() {
        Disposable subscription = mTravisRestClient.getApiService()
                .getBuilds(mRepoSlug)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((buildHistory, throwable) -> {
                    if (throwable == null) {
                        getView().updateBuildHistory(buildHistory);
                    } else {
                        getView().showBuildHistoryLoadingError(throwable.getMessage());
                    }
                });
        mSubscriptions.add(subscription);
    }

    /**
     * Starts loading branches
     */
    public void loadBranches() {
        Disposable subscription = mTravisRestClient.getApiService()
                .getBranches(mRepoSlug)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((branches, throwable) -> {
                    if (throwable == null) {
                        getView().updateBranches(branches);
                    } else {
                        getView().showBranchesLoadingError(throwable.getMessage());
                    }
                });
        mSubscriptions.add(subscription);
    }

    /**
     * Starts loading requests
     */
    public void loadRequests() {
        Disposable subscription = Single.zip(mTravisRestClient.getApiService().getRequests(mRepoSlug),
                mTravisRestClient.getApiService().getPullRequestBuilds(mRepoSlug),
                (requests, buildHistory) -> {
                    requests.setBuilds(buildHistory.getBuilds());
                    return requests;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((requests, throwable) -> {
                    if (throwable == null) {
                        getView().updatePullRequests(requests);
                    } else {
                        getView().showPullRequestsLoadingError(throwable.getMessage());
                    }
                });
        mSubscriptions.add(subscription);
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
