package com.khmelenko.lab.varis.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.text.TextUtils
import com.khmelenko.lab.varis.common.Constants
import com.khmelenko.lab.varis.network.response.Repo
import com.khmelenko.lab.varis.network.response.User
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import com.khmelenko.lab.varis.storage.AppSettings
import com.khmelenko.lab.varis.storage.CacheStorage
import com.khmelenko.lab.varis.util.StringUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiConsumer
import io.reactivex.schedulers.Schedulers

/**
 * ViewModel for Repositories screen
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class RepositoriesViewModel(private val restClient: TravisRestClient,
                            private val cacheStorage: CacheStorage,
                            private val appSettings: AppSettings
) : ViewModel() {

    private var user = cacheStorage.restoreUser()

    private val reposState = MutableLiveData<RepositoriesState>()

    private val subscriptions = CompositeDisposable()

    fun init(){
        postState(RepositoriesState.UserData(user))
        postState(RepositoriesState.Loading)

        reloadRepos()
    }

    fun release() {
        subscriptions.clear()
    }

    fun state() : LiveData<RepositoriesState> {
        return reposState;
    }

    fun isUserLoggedIn(): Boolean {
        return !StringUtils.isEmpty(appSettings.accessToken)
    }

    fun reloadRepos() {
        postState(RepositoriesState.Loading)
        if (StringUtils.isEmpty(appSettings.accessToken)) {
            val subscription = restClient.apiService
                    .getRepos("")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(reposHandler())

            subscriptions.add(subscription)
        } else {
            val subscription = restClient.apiService.user
                    .doOnSuccess { this.cacheUserData(it) }
                    .flatMap { restClient.apiService.getUserRepos(user?.login!!) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(reposHandler())
            subscriptions.add(subscription)
        }
    }

    /**
     * Does user logout
     */
    fun userLogout() {
        cacheStorage.deleteUser()
        cacheStorage.deleteRepos()
        appSettings.putAccessToken("")

        // reset back to open source url
        appSettings.putServerUrl(Constants.OPEN_SOURCE_TRAVIS_URL)
        restClient.updateTravisEndpoint(appSettings.serverUrl)
    }

    private fun reposHandler(): BiConsumer<List<Repo>, Throwable> {
        return BiConsumer { repos, throwable ->
            if (user != null) {
                postState(RepositoriesState.UserData(user))
            }

            if (throwable == null) {
                handleReposLoaded(repos)
            } else {
                handleLoadingFailed(throwable)
            }
        }
    }

    private fun cacheUserData(user: User) {
        this.user = user

        // cache user data
        cacheStorage.saveUser(user)
    }

    private fun handleReposLoaded(repos: List<Repo>) {
        postState(RepositoriesState.ReposList(repos))
        cacheStorage.saveRepos(repos)
    }

    private fun handleLoadingFailed(throwable: Throwable?) {
        postState(RepositoriesState.Error(throwable?.message))
    }

    private fun postState(newState: RepositoriesState) {
        reposState.value = newState
    }
}
