package com.khmelenko.lab.varis.repodetails

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.khmelenko.lab.varis.network.response.BuildHistory
import com.khmelenko.lab.varis.network.response.Requests
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Repository details presenter
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class RepoDetailsViewModel(private val travisRestClient: TravisRestClient) : ViewModel() {

    private val repoDetailsState = MutableLiveData<RepoDetailsState>()

    var repoSlug: String = ""

    private val subscriptions = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        subscriptions.clear()
    }

    fun state(): LiveData<RepoDetailsState> {
        return repoDetailsState
    }

    /**
     * Starts loading build history
     */
    fun loadBuildsHistory() {
        val subscription = travisRestClient.apiService
                .getBuilds(repoSlug)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { buildHistory, throwable ->
                    if (throwable == null) {
                        repoDetailsState.postValue(RepoDetailsState.BuildHistoryLoaded(buildHistory))
                    } else {
                        repoDetailsState.postValue(RepoDetailsState.BuildHistoryError(throwable.message))
                    }
                }
        subscriptions.add(subscription)
    }

    /**
     * Starts loading branches
     */
    fun loadBranches() {
        val subscription = travisRestClient.apiService
                .getBranches(repoSlug)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { branches, throwable ->
                    if (throwable == null) {
                        repoDetailsState.postValue(RepoDetailsState.BranchesLoaded(branches))
                    } else {
                        repoDetailsState.postValue(RepoDetailsState.BranchesError(throwable.message))
                    }
                }
        subscriptions.add(subscription)
    }

    /**
     * Starts loading requests
     */
    fun loadRequests() {
        val subscription = Single.zip<Requests, BuildHistory, Requests>(
                travisRestClient.apiService.getRequests(repoSlug),
                travisRestClient.apiService.getPullRequestBuilds(repoSlug),
                BiFunction { requests, buildHistory ->
                    requests.builds = buildHistory.builds
                    requests
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { requests, throwable ->
                    if (throwable == null) {
                        repoDetailsState.postValue(RepoDetailsState.PullRequestsLoaded(requests))
                    } else {
                        repoDetailsState.postValue(RepoDetailsState.PullRequestsError(throwable.message))
                    }
                }
        subscriptions.add(subscription)
    }

    /**
     * Loads repository details data
     */
    fun loadData() {
        loadBuildsHistory()
        loadBranches()
        loadRequests()
    }
}
