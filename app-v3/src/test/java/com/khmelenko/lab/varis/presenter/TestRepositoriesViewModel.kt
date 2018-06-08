package com.khmelenko.lab.varis.presenter

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.khmelenko.lab.varis.RxJavaRules
import com.khmelenko.lab.varis.common.Constants
import com.khmelenko.lab.varis.dagger.DaggerTestComponent
import com.khmelenko.lab.varis.network.response.Repo
import com.khmelenko.lab.varis.network.response.User
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import com.khmelenko.lab.varis.repositories.RepositoriesState
import com.khmelenko.lab.varis.repositories.RepositoriesViewModel
import com.khmelenko.lab.varis.storage.AppSettings
import com.khmelenko.lab.varis.storage.CacheStorage
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import javax.inject.Inject

/**
 * Testing [com.khmelenko.lab.varis.repositories.RepositoriesViewModel]
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class TestRepositoriesViewModel {

    @get:Rule
    val rxJavaRules = RxJavaRules()

    @get:Rule
    var testRule: TestRule = InstantTaskExecutorRule()

    @Inject
    lateinit var travisRestClient: TravisRestClient

    @Inject
    lateinit var cacheStorage: CacheStorage

    @Inject
    lateinit var appSettings: AppSettings

    private lateinit var repositoriesViewModel: RepositoriesViewModel

    private val stateObserver = mock<Observer<RepositoriesState>>()

    @Before
    fun setup() {
        val component = DaggerTestComponent.builder().build()
        component.inject(this)

        val responseData = ArrayList<Repo>()
        whenever(travisRestClient.apiService.getRepos("")).thenReturn(Single.just(responseData))

        repositoriesViewModel = RepositoriesViewModel(travisRestClient, cacheStorage, appSettings)
        repositoriesViewModel.state().observeForever(stateObserver)
    }

    @Test
    fun testReloadRepos() {
        val responseData = ArrayList<Repo>()
        whenever(travisRestClient.apiService.repos).thenReturn(Single.just(responseData))

        repositoriesViewModel.reloadRepos()

        verify(stateObserver).onChanged(RepositoriesState.Loading)
        verify(stateObserver).onChanged(RepositoriesState.ReposList(responseData))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testReloadReposWithToken() {
        val user = User()
        user.login = "login"
        whenever(travisRestClient.apiService.user).thenReturn(Single.just(user))
        whenever(appSettings.accessToken).thenReturn("token")

        repositoriesViewModel.reloadRepos()

        verify(stateObserver).onChanged(RepositoriesState.Loading)
        verify(stateObserver).onChanged(RepositoriesState.UserData(user))
        verify(cacheStorage).saveUser(ArgumentMatchers.eq(user))
    }

    @Test
    fun testUserLogout() {
        whenever(appSettings.serverUrl).thenReturn(Constants.OPEN_SOURCE_TRAVIS_URL)

        repositoriesViewModel.userLogout()
        verify(cacheStorage).deleteUser()
        verify(cacheStorage).deleteRepos()
        verify(travisRestClient).updateTravisEndpoint(ArgumentMatchers.eq(Constants.OPEN_SOURCE_TRAVIS_URL))
    }

}
