package com.khmelenko.lab.varis.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.khmelenko.lab.varis.RxJavaRules
import com.khmelenko.lab.varis.dagger.DaggerTestComponent
import com.khmelenko.lab.varis.network.response.Branches
import com.khmelenko.lab.varis.network.response.BuildHistory
import com.khmelenko.lab.varis.network.response.Requests
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import com.khmelenko.lab.varis.repodetails.RepoDetailsState
import com.khmelenko.lab.varis.repodetails.RepoDetailsViewModel
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import javax.inject.Inject

/**
 * Testing [RepoDetailsViewModel]
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class TestRepoDetailsViewModel {

    @get:Rule
    var rxJavaRules = RxJavaRules()

    @get:Rule
    var testRule: TestRule = InstantTaskExecutorRule()

    @Inject
    lateinit var travisRestClient: TravisRestClient

    private lateinit var repoDetailsViewModel: RepoDetailsViewModel

    private val stateObserver = mock<Observer<RepoDetailsState>>()

    @Before
    fun setup() {
        val component = DaggerTestComponent.builder().build()
        component.inject(this)

        repoDetailsViewModel = RepoDetailsViewModel(travisRestClient)
        repoDetailsViewModel.state().observeForever(stateObserver)
    }

    @Test
    fun testLoadBuildsHistory() {
        val slug = "test"
        val buildHistory = BuildHistory()
        buildHistory.builds = emptyList()
        buildHistory.commits = emptyList()

        whenever(travisRestClient.apiService.getBuilds(slug)).thenReturn(Single.just(buildHistory))

        repoDetailsViewModel.repoSlug = slug
        repoDetailsViewModel.loadBuildsHistory()

        verify(stateObserver).onChanged(RepoDetailsState.BuildHistoryLoaded(buildHistory))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testLoadBuildsHistoryFailed() {
        val slug = "test"

        val errorMsg = "error"
        val exception = Exception(errorMsg)
        whenever(travisRestClient.apiService.getBuilds(slug)).thenReturn(Single.error(exception))

        repoDetailsViewModel.repoSlug = slug
        repoDetailsViewModel.loadBuildsHistory()

        verify(stateObserver).onChanged(RepoDetailsState.BuildHistoryError(errorMsg))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testLoadBranches() {
        val slug = "test"
        val branches = Branches()
        branches.branches = emptyList()
        branches.commits = emptyList()

        whenever(travisRestClient.apiService.getBranches(slug)).thenReturn(Single.just(branches))

        repoDetailsViewModel.repoSlug = slug
        repoDetailsViewModel.loadBranches()

        verify(stateObserver).onChanged(RepoDetailsState.BranchesLoaded(branches))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testLoadBranchesFailed() {
        val slug = "test"

        val errorMsg = "error"
        val exception = Exception(errorMsg)
        whenever(travisRestClient.apiService.getBranches(slug)).thenReturn(Single.error(exception))

        repoDetailsViewModel.repoSlug = slug
        repoDetailsViewModel.loadBranches()

        verify(stateObserver).onChanged(RepoDetailsState.BranchesError(errorMsg))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testLoadRequests() {
        val slug = "test"
        val requests = Requests(emptyList(), emptyList(), emptyList())
        val buildHistory = BuildHistory()
        buildHistory.builds = emptyList()
        buildHistory.commits = emptyList()

        whenever(travisRestClient.apiService.getRequests(slug)).thenReturn(Single.just(requests))
        whenever(travisRestClient.apiService.getPullRequestBuilds(slug)).thenReturn(Single.just(buildHistory))

        repoDetailsViewModel.repoSlug = slug
        repoDetailsViewModel.loadRequests()

        verify(stateObserver).onChanged(RepoDetailsState.PullRequestsLoaded(requests))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testLoadRequestsFailed() {
        val slug = "test"

        val errorMsg = "error"
        val exception = Exception(errorMsg)
        whenever(travisRestClient.apiService.getRequests(slug)).thenReturn(Single.error(exception))
        whenever(travisRestClient.apiService.getPullRequestBuilds(slug)).thenReturn(Single.error(exception))

        repoDetailsViewModel.repoSlug = slug
        repoDetailsViewModel.loadRequests()

        verify(stateObserver).onChanged(RepoDetailsState.PullRequestsError(errorMsg))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testSetRepoSlug() {
        val slug = "test"
        repoDetailsViewModel.repoSlug = slug
        assertEquals(slug, repoDetailsViewModel.repoSlug)
    }

    @Test
    fun testLoadData() {
        val slug = "test"
        val requests = Requests(emptyList(), emptyList(), emptyList())
        val buildHistory = BuildHistory()
        buildHistory.builds = emptyList()
        buildHistory.commits = emptyList()
        val branches = Branches()
        branches.branches = emptyList()
        branches.commits = emptyList()

        whenever(travisRestClient.apiService.getBuilds(slug)).thenReturn(Single.just(buildHistory))
        whenever(travisRestClient.apiService.getBranches(slug)).thenReturn(Single.just(branches))
        whenever(travisRestClient.apiService.getRequests(slug)).thenReturn(Single.just(requests))
        whenever(travisRestClient.apiService.getPullRequestBuilds(slug)).thenReturn(Single.just(buildHistory))

        repoDetailsViewModel.repoSlug = slug
        repoDetailsViewModel.loadData()

        verify(stateObserver).onChanged(RepoDetailsState.BuildHistoryLoaded(buildHistory))
        verify(stateObserver).onChanged(RepoDetailsState.BranchesLoaded(branches))
        verify(stateObserver).onChanged(RepoDetailsState.PullRequestsLoaded(requests))
        verifyNoMoreInteractions(stateObserver)
    }
}
