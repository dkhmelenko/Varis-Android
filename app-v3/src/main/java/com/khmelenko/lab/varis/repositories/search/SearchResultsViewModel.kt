package com.khmelenko.lab.varis.repositories.search


import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.khmelenko.lab.varis.network.response.Repo
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import com.khmelenko.lab.varis.util.StringUtils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Search results presenter
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class SearchResultsViewModel(private val travisRestClient: TravisRestClient) : ViewModel() {

    private val searchState = MutableLiveData<SearchState>()

    private val subscriptions: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        subscriptions.clear()
    }

    fun state(): LiveData<SearchState> = searchState

    /**
     * Starts repository search
     *
     * @param query Query string for search
     */
    fun startRepoSearch(query: String) {
        val reposSingle = if (!StringUtils.isEmpty(query)) {
            travisRestClient.apiService.getRepos(query)
        } else {
            travisRestClient.apiService.repos
        }
        searchState.postValue(SearchState.Loading)
        val subscription = reposSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { repos, throwable ->
                   searchState
                    if (throwable == null) {
                        searchState.postValue(SearchState.ReposList(repos))
                    } else {
                        searchState.postValue(SearchState.Error(throwable.message))
                    }
                }
        subscriptions.add(subscription)
    }

}
