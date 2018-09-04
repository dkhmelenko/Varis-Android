package com.khmelenko.lab.varis.viewmodel

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.khmelenko.lab.varis.RxJavaRules
import com.khmelenko.lab.varis.builddetails.BuildDetailsState
import com.khmelenko.lab.varis.builddetails.BuildsDetailsViewModel
import com.khmelenko.lab.varis.dagger.DaggerTestComponent
import com.khmelenko.lab.varis.log.LogEntryComponent
import com.khmelenko.lab.varis.log.LogsParser
import com.khmelenko.lab.varis.network.response.Build
import com.khmelenko.lab.varis.network.response.BuildDetails
import com.khmelenko.lab.varis.network.response.Commit
import com.khmelenko.lab.varis.network.response.Job
import com.khmelenko.lab.varis.network.retrofit.raw.RawClient
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import com.khmelenko.lab.varis.storage.AppSettings
import com.khmelenko.lab.varis.storage.CacheStorage
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import javax.inject.Inject

/**
 * Testing [BuildsDetailsViewModel]
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class TestBuildDetailsViewModel {

    @get:Rule
    var rxJavaRules = RxJavaRules()

    @get:Rule
    var testRule: TestRule = InstantTaskExecutorRule()

    @Inject
    lateinit var cacheStorage: CacheStorage

    @Inject
    lateinit var rawClient: RawClient

    @Inject
    lateinit var travisRestClient: TravisRestClient

    @Inject
    lateinit var appSettings: AppSettings

    @Inject
    lateinit var logsParser: LogsParser

    private lateinit var buildsDetailsViewModel: BuildsDetailsViewModel

    private val stateObserver = mock<Observer<BuildDetailsState>>()

    @Before
    fun setup() {
        val component = DaggerTestComponent.builder().build()
        component.inject(this)

        buildsDetailsViewModel = BuildsDetailsViewModel(travisRestClient, rawClient, cacheStorage,
                appSettings, logsParser)
        buildsDetailsViewModel.state().observeForever(stateObserver)
    }

    @Test
    fun testStartLoadingLogNoToken() {
        val jobId = 1L

        val expectedUrl = "https://sample.org"
        val log = "log"

        val logEntry = object : LogEntryComponent {
            override fun toHtml(): String = ""
        }

        whenever(rawClient.apiService.getLog(jobId.toString())).thenReturn(Single.just(expectedUrl))
        whenever(rawClient.getLogUrl(jobId)).thenReturn(expectedUrl)
        whenever(rawClient.singleStringRequest(expectedUrl)).thenReturn(Single.just(log))
        whenever(logsParser.parseLog(log)).thenReturn(logEntry)

        buildsDetailsViewModel.startLoadingLog(jobId)

        verify(stateObserver).onChanged(BuildDetailsState.LogEntryLoaded(logEntry))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testStartLoadingLogWithToken() {
        val jobId = 1L

        val expectedUrl = "https://sample.org"
        val accessToken = "test"
        val authToken = "token $accessToken"
        val log = "log"

        val logEntry = object : LogEntryComponent {
            override fun toHtml(): String = ""
        }

        whenever(rawClient.apiService.getLog(authToken, jobId.toString())).thenReturn(Single.just(""))
        whenever(rawClient.getLogUrl(any())).thenReturn(expectedUrl)
        whenever(appSettings.accessToken).thenReturn(accessToken)
        whenever(rawClient.singleStringRequest(expectedUrl)).thenReturn(Single.just(log))
        whenever(logsParser.parseLog(log)).thenReturn(logEntry)

        buildsDetailsViewModel.startLoadingLog(jobId)

        verify(stateObserver).onChanged(BuildDetailsState.LogEntryLoaded(logEntry))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testStartLoadingLogFailed() {
        val jobId = 1L

        val errorMsg = "error"
        val exception = Exception(errorMsg)
        whenever(rawClient.apiService.getLog(jobId.toString())).thenReturn(Single.error(exception))

        buildsDetailsViewModel.startLoadingLog(jobId)

        verify(stateObserver).onChanged(BuildDetailsState.Error(errorMsg))
        verify(stateObserver).onChanged(BuildDetailsState.LogError)
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testStartLoadingDataSingleJob() {
        val buildId = 1L
        val slug = "test"
        val expectedUrl = "https://sample.org"
        val log = "log"

        val build = Build()
        val commit = Commit()
        val job = Job()
        val jobs = listOf(job)
        val buildDetails = BuildDetails()
        buildDetails.build = build
        buildDetails.commit = commit
        buildDetails.jobs = jobs

        val accessToken = "test"
        val authToken = "token $accessToken"

        val logEntry = object : LogEntryComponent {
            override fun toHtml(): String = ""
        }

        whenever(rawClient.apiService.getLog(authToken, job.id.toString())).thenReturn(Single.just(""))
        whenever(rawClient.getLogUrl(any())).thenReturn(expectedUrl)
        whenever(rawClient.singleStringRequest(expectedUrl)).thenReturn(Single.just(log))
        whenever(logsParser.parseLog(any())).thenReturn(logEntry)
        whenever(travisRestClient.apiService.getBuild(slug, buildId)).thenReturn(Single.just(buildDetails))
        whenever(appSettings.accessToken).thenReturn(accessToken)

        buildsDetailsViewModel.startLoadingData(null, slug, buildId)

        verify(stateObserver).onChanged(BuildDetailsState.Loading)
        verify(stateObserver).onChanged(BuildDetailsState.BuildDetailsLoaded(buildDetails))
        verify(stateObserver).onChanged(BuildDetailsState.BuildLogsLoaded)
        verify(stateObserver).onChanged(BuildDetailsState.BuildAdditionalActionsAvailable(buildDetails))
        verify(stateObserver).onChanged(BuildDetailsState.LogEntryLoaded(logEntry))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testStartLoadingDataManyJobs() {
        val buildId = 1L
        val slug = "test"

        val build = Build()
        val commit = Commit()
        val job1 = Job()
        val job2 = Job()
        val jobs = listOf(job1, job2)
        val buildDetails = BuildDetails()
        buildDetails.build = build
        buildDetails.commit = commit
        buildDetails.jobs = jobs

        whenever(travisRestClient.apiService.getBuild(slug, buildId)).thenReturn(Single.just(buildDetails))

        buildsDetailsViewModel.startLoadingData(null, slug, buildId)

        verify(stateObserver).onChanged(BuildDetailsState.Loading)
        verify(stateObserver).onChanged(BuildDetailsState.BuildDetailsLoaded(buildDetails))
        verify(stateObserver).onChanged(BuildDetailsState.BuildJobsLoaded(jobs))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testStartLoadingDataIntentUrl() {
        val buildId = 188971232L
        val slug = "dkhmelenko/Varis-Android"
        val intentUrl = "https://travis-ci.org/dkhmelenko/Varis-Android/builds/188971232"

        whenever(rawClient.singleRequest(intentUrl)).thenReturn(Single.just(intentResponse()))

        buildsDetailsViewModel.startLoadingData(intentUrl, slug, buildId)
    }

    @Test
    fun testStartLoadingDataFailed() {
        val buildId = 1L
        val slug = "test"

        val errorMsg = "error"
        val exception = Exception(errorMsg)
        whenever(travisRestClient.apiService.getBuild(slug, buildId)).thenReturn(Single.error(exception))

        buildsDetailsViewModel.startLoadingData(null, slug, buildId)

        verify(stateObserver).onChanged(BuildDetailsState.Loading)
        verify(stateObserver).onChanged(BuildDetailsState.Error(errorMsg))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testStartLoadingDataIntentUrlFailed() {
        val buildId = 0L
        val slug = "dkhmelenko/Varis-Android"
        val intentUrl = "https://travis-ci.org/dkhmelenko/Varis-Android/builds"

        val response = intentResponse()
        val errorMsg = "error"

        whenever(rawClient.singleRequest(intentUrl)).thenReturn(Single.just(response))
        whenever(travisRestClient.apiService.getBuild(any<String>(), any())).thenReturn(Single.error(Exception(errorMsg)))

        buildsDetailsViewModel.startLoadingData(intentUrl, slug, buildId)

        verify(stateObserver).onChanged(BuildDetailsState.Loading)
        verify(stateObserver).onChanged(BuildDetailsState.Error(errorMsg))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testRestartBuild() {
        val buildDetails = BuildDetails()

        whenever(travisRestClient.apiService.restartBuild(any(), any())).thenReturn(Single.just(Any()))
        whenever(travisRestClient.apiService.getBuild(any<String>(), any())).thenReturn(Single.just(buildDetails))

        buildsDetailsViewModel.restartBuild()

        // TODO Extend tests here
    }

    @Test
    fun testRestartBuildFailed() {
        val errorMsg = "error"
        val exception = Exception(errorMsg)

        whenever(travisRestClient.apiService.restartBuild(any(), any())).thenReturn(Single.error(exception))
        whenever(travisRestClient.apiService.getBuild(any<String>(), any())).thenReturn(Single.error(exception))

        buildsDetailsViewModel.restartBuild()

        verify(stateObserver).onChanged(BuildDetailsState.Error(errorMsg))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testCancelBuild() {
        val buildDetails = BuildDetails()

        whenever(travisRestClient.apiService.cancelBuild(any(), any())).thenReturn(Single.just(Any()))
        whenever(travisRestClient.apiService.getBuild(any<String>(), any())).thenReturn(Single.just(buildDetails))

        buildsDetailsViewModel.cancelBuild()

        // TODO Extend tests here
    }

    @Test
    fun testCancelBuildFailed() {
        val errorMsg = "error"
        val exception = Exception(errorMsg)

        whenever(travisRestClient.apiService.cancelBuild(any(), any())).thenReturn(Single.error(exception))
        whenever(travisRestClient.apiService.getBuild(any<String>(), any())).thenReturn(Single.error(exception))

        buildsDetailsViewModel.cancelBuild()

        verify(stateObserver).onChanged(BuildDetailsState.Error(errorMsg))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testCanUserContributeToRepoTrue() {
        val buildId = 188971232L
        val slug = "dkhmelenko/Varis-Android"
        val intentUrl = "https://travis-ci.org/dkhmelenko/Varis-Android/builds/188971232"

        whenever(rawClient.singleRequest(intentUrl)).thenReturn(Single.just(intentResponse()))
        val repos = arrayOf("Repo1", "Repo2", "Repo3", slug, "Repo4")
        whenever(cacheStorage.restoreRepos()).thenReturn(repos)

        buildsDetailsViewModel.startLoadingData(intentUrl, slug, buildId)
        val actual = buildsDetailsViewModel.canUserContributeToRepo()
        assertEquals(true, actual)
    }

    @Test
    fun testCanUserContributeToRepoFalse() {
        val buildId = 188971232L
        val slug = "dkhmelenko/Varis-Android"
        val intentUrl = "https://travis-ci.org/dkhmelenko/Varis-Android/builds/188971232"

        whenever(rawClient.singleRequest(intentUrl)).thenReturn(Single.just(intentResponse()))
        val repos = arrayOf("Repo1", "Repo2", "Repo3")
        whenever(cacheStorage.restoreRepos()).thenReturn(repos)

        buildsDetailsViewModel.startLoadingData(intentUrl, slug, buildId)
        val actual = buildsDetailsViewModel.canUserContributeToRepo()
        assertEquals(false, actual)
    }

    private fun intentResponse(): Response {
        val expectedUrl = "https://sample.org"
        val request = Request.Builder()
                .url(expectedUrl)
                .build()
        return Response.Builder()
                .request(request)
                .message("no body")
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .build()
    }
}
