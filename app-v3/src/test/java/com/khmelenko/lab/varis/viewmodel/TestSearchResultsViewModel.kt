package com.khmelenko.lab.varis.viewmodel

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.khmelenko.lab.varis.RxJavaRules
import com.khmelenko.lab.varis.dagger.DaggerTestComponent
import com.khmelenko.lab.varis.network.response.Repo
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import com.khmelenko.lab.varis.repositories.search.SearchResultsViewModel
import com.khmelenko.lab.varis.repositories.search.SearchState
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import javax.inject.Inject

/**
 * Testing [SearchResultsViewModel]
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class TestSearchResultsViewModel {

    @get:Rule
    var rxJavaRules = RxJavaRules()

    @get:Rule
    var testRule: TestRule = InstantTaskExecutorRule()

    @Inject
    lateinit var travisRestClient: TravisRestClient

    lateinit var searchResultsViewModel: SearchResultsViewModel

    private val stateObserver = mock<Observer<SearchState>>()

    @Before
    fun setup() {
        val component = DaggerTestComponent.builder().build()
        component.inject(this)

        searchResultsViewModel = SearchResultsViewModel(travisRestClient)

        searchResultsViewModel.state().observeForever(stateObserver)
    }

    @Test
    fun testStartRepoSearch() {
        val searchQuery = "test"
        val responseData = ArrayList<Repo>()
        whenever(travisRestClient.apiService.getRepos(searchQuery)).thenReturn(Single.just(responseData))

        searchResultsViewModel.startRepoSearch(searchQuery)

        verify(travisRestClient.apiService).getRepos(searchQuery)

        verify(stateObserver).onChanged(SearchState.Loading)
        verify(stateObserver).onChanged(SearchState.ReposList(responseData))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testStartRepoSearchFailed() {
        val searchQuery = "test"

        val errorMsg = "error"
        val exception = Exception(errorMsg)
        whenever(travisRestClient.apiService.getRepos(searchQuery)).thenReturn(Single.error(exception))

        searchResultsViewModel.startRepoSearch(searchQuery)

        verify(travisRestClient.apiService).getRepos(searchQuery)

        verify(stateObserver).onChanged(SearchState.Loading)
        verify(stateObserver).onChanged(SearchState.Error(errorMsg))
        verifyNoMoreInteractions(stateObserver)
    }
}
