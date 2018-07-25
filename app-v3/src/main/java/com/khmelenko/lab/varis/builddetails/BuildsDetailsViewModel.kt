package com.khmelenko.lab.varis.builddetails

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.khmelenko.lab.varis.log.LogsParser
import com.khmelenko.lab.varis.network.response.BuildDetails
import com.khmelenko.lab.varis.network.retrofit.raw.RawClient
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import com.khmelenko.lab.varis.storage.AppSettings
import com.khmelenko.lab.varis.storage.CacheStorage
import com.khmelenko.lab.varis.util.StringUtils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.HttpException
import java.net.MalformedURLException
import java.net.URL

private const val LOAD_LOG_MAX_ATTEMPT = 3

/**
 * ViewModel for BuildDetails
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class BuildsDetailsViewModel(private val travisRestClient: TravisRestClient,
                             private val rawClient: RawClient,
                             private val cache: CacheStorage,
                             private val appSettings: AppSettings,
                             private val logsParser: LogsParser) : ViewModel() {

    private val subscriptions = CompositeDisposable()

    private var repoSlug = ""
    private var buildId: Long = 0
    private var jobId: Long = 0

    private val buildState = MutableLiveData<BuildDetailsState>()

    override fun onCleared() {
        subscriptions.clear()
    }

    fun state(): LiveData<BuildDetailsState> {
        return buildState
    }

    /**
     * Starts loading log file
     *
     * @param jobId Job ID
     */
    fun startLoadingLog(jobId: Long) {
        this.jobId = jobId
        val accessToken = appSettings.accessToken
        val responseSingle = if (StringUtils.isEmpty(accessToken)) {
            rawClient.apiService.getLog(this.jobId.toString())
        } else {
            val auth = String.format("token %1\$s", appSettings.accessToken)
            rawClient.apiService.getLog(auth, this.jobId.toString())
        }

        val subscription = responseSingle.subscribeOn(Schedulers.io())
                .map { rawClient.getLogUrl(this.jobId) }
                .onErrorResumeNext { throwable ->
                    var redirectUrl: String? = ""
                    if (throwable is HttpException) {
                        val headers = throwable.response().headers()
                        for (header in headers.names()) {
                            if (header == "Location") {
                                redirectUrl = headers.get(header)
                                break
                            }
                        }
                        Single.just(redirectUrl!!)
                    } else {
                        Single.error(throwable)
                    }
                }
                .retry(LOAD_LOG_MAX_ATTEMPT.toLong())
                .map { rawClient.singleStringRequest(it) }
                .map { response -> logsParser.parseLog(response.blockingGet()) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { log, throwable ->
                    if (throwable == null) {
                        postState(BuildDetailsState.LogEntryLoaded(log))
                    } else {
                        postState(BuildDetailsState.LogError)
                        postState(BuildDetailsState.Error(throwable.message))
                    }
                }

        subscriptions.add(subscription)
    }

    /**
     * Starts loading data
     *
     * @param intentUrl Intent URL
     * @param repoSlug  Repository slug
     * @param buildId   Build ID
     */
    fun startLoadingData(intentUrl: String?, repoSlug: String, buildId: Long) {
        this.repoSlug = repoSlug
        this.buildId = buildId

        val buildDetailsSingle: Single<BuildDetails>

        if (!StringUtils.isEmpty(intentUrl)) {
            buildDetailsSingle = rawClient.singleRequest(intentUrl)
                    .doOnSuccess { response ->
                        var redirectUrl: String? = intentUrl
                        if (response.isRedirect) {
                            redirectUrl = response.header("Location", "")
                        }
                        parseIntentUrl(redirectUrl)
                    }
                    .flatMap { travisRestClient.apiService.getBuild(this.repoSlug, this.buildId) }
        } else {
            buildDetailsSingle = travisRestClient.apiService.getBuild(this.repoSlug, this.buildId)
        }

        val subscription = buildDetailsSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { buildDetails, throwable ->
                    if (throwable == null) {
                        handleBuildDetails(buildDetails)
                    } else {
                        handleLoadingFailed(throwable)
                    }
                }

        subscriptions.add(subscription)

        postState(BuildDetailsState.Loading)
    }

    /**
     * Parses intent URL
     *
     * @param intentUrl Intent URL
     */
    private fun parseIntentUrl(intentUrl: String?) {
        val ownerIndex = 1
        val repoNameIndex = 2
        val buildIdIndex = 4
        val pathLength = 5

        try {
            val url = URL(intentUrl)
            val path = url.path
            val items = path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (items.size >= pathLength) {
                repoSlug = String.format("%s/%s", items[ownerIndex], items[repoNameIndex])
                buildId = items[buildIdIndex].toLong()
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

    }

    /**
     * Restarts build process
     */
    fun restartBuild() {
        val emptyBody = RequestBody.create(MediaType.parse("application/json"), "")
        val subscription = travisRestClient.apiService
                .restartBuild(buildId, emptyBody)
                .onErrorReturn { Any() }
                .flatMap { travisRestClient.apiService.getBuild(repoSlug, buildId) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { buildDetails, throwable ->
                    if (throwable == null) {
                        handleBuildDetails(buildDetails)
                    } else {
                        handleLoadingFailed(throwable)
                    }
                }

        subscriptions.add(subscription)
    }

    /**
     * Cancels build process
     */
    fun cancelBuild() {
        val emptyBody = RequestBody.create(MediaType.parse("application/json"), "")
        val subscription = travisRestClient.apiService
                .cancelBuild(buildId, emptyBody)
                .onErrorReturn { Any() }
                .flatMap { travisRestClient.apiService.getBuild(repoSlug, buildId) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { buildDetails, throwable ->
                    if (throwable == null) {
                        handleBuildDetails(buildDetails)
                    } else {
                        handleLoadingFailed(throwable)
                    }
                }

        subscriptions.add(subscription)
    }

    /**
     * Defines whether the user can contribute to the repository or not
     *
     * @return True if user can contribute to the repository. False otherwise
     */
    fun canUserContributeToRepo(): Boolean {
        var canContributeToRepo = false
        val userRepos = cache.restoreRepos()
        for (repo in userRepos) {
            if (repo == repoSlug) {
                canContributeToRepo = true
                break
            }
        }
        return canContributeToRepo
    }

    private fun handleBuildDetails(buildDetails: BuildDetails) {
        postState(BuildDetailsState.BuildDetailsLoaded(buildDetails))

        if (buildDetails.jobs.size > 1) {
            postState(BuildDetailsState.BuildJobsLoaded(buildDetails.jobs))

        } else if (buildDetails.jobs.size == 1) {
            postState(BuildDetailsState.BuildLogsLoaded)

            val job = buildDetails.jobs[0]
            startLoadingLog(job.id)
        }

        // if user logged in, show additional actions for the repo
        val appToken = appSettings.accessToken
        if (!StringUtils.isEmpty(appToken)) {
            postState(BuildDetailsState.BuildAdditionalActionsAvailable(buildDetails))
        }
    }

    private fun handleLoadingFailed(throwable: Throwable) {
        postState(BuildDetailsState.Error(throwable.message))
    }

    private fun postState(state: BuildDetailsState) {
        buildState.value = state
    }
}
