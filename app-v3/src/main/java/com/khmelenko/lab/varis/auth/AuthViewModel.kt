package com.khmelenko.lab.varis.auth

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.TextUtils
import com.khmelenko.lab.varis.network.request.AccessTokenRequest
import com.khmelenko.lab.varis.network.request.AuthorizationRequest
import com.khmelenko.lab.varis.network.response.AccessToken
import com.khmelenko.lab.varis.network.response.Authorization
import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient
import com.khmelenko.lab.varis.network.retrofit.github.TWO_FACTOR_HEADER
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import com.khmelenko.lab.varis.storage.AppSettings
import com.khmelenko.lab.varis.util.EncryptionUtils
import com.khmelenko.lab.varis.util.StringUtils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.net.HttpURLConnection
import java.util.*

/**
 * Authentication ViewModel
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class AuthViewModel(
        private val travisRestClient: TravisRestClient,
        private val gitHubRestClient: GitHubRestClient,
        private val appSettings: AppSettings)
    : ViewModel() {

    private val subscriptions = CompositeDisposable()

    private lateinit var basicAuth: String
    private lateinit var authorization: Authorization
    private var securityCode: String? = null

    private var securityCodeInput: Boolean = false

    private val authState = MutableLiveData<AuthState>()

    val serverUrl: String
        get() = appSettings.serverUrl

    val isCodeInput: Boolean
        get() = securityCodeInput

    fun state(): LiveData<AuthState> = authState

    override fun onCleared() {
        subscriptions.clear()
    }

    /**
     * Updates server endpoint
     *
     * @param newServer New server endpoint
     */
    fun updateServer(newServer: String) {
        appSettings.putServerUrl(newServer)
        travisRestClient.updateTravisEndpoint(newServer)
    }

    /**
     * Executes login action
     *
     * @param userName Username
     * @param password Password
     */
    fun login(userName: String, password: String) {
        basicAuth = EncryptionUtils.generateBasicAuthorization(userName, password)

        doLogin(getAuthorizationJob(false))
    }

    /**
     * Executes two-factor auth call
     *
     * @param securityCode Security code
     */
    fun twoFactorAuth(securityCode: String) {
        this.securityCode = securityCode
        doLogin(getAuthorizationJob(true))
    }

    private fun getAuthorizationJob(twoFactorAuth: Boolean): Single<Authorization> {
        return if (twoFactorAuth) {
            gitHubRestClient.apiService.createNewAuthorization(basicAuth, securityCode!!, prepareAuthorizationRequest())
        } else {
            gitHubRestClient.apiService.createNewAuthorization(basicAuth, prepareAuthorizationRequest())
        }
    }

    private fun doLogin(authorizationJob: Single<Authorization>) {
        postState(AuthState.Loading)

        val subscription = authorizationJob
                .flatMap { doAuthorization(it) }
                .doOnSuccess { saveAccessToken(it) }
                .doAfterSuccess { cleanUpAfterAuthorization() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { authorization, throwable ->
                    if (throwable == null) {
                        postState(AuthState.Success)
                    } else {
                        if (throwable is HttpException && isTwoFactorAuthRequired(throwable)) {
                            securityCodeInput = true
                            postState(AuthState.ShowTwoFactorAuth)
                        } else {
                            postState(AuthState.AuthError(throwable.message))
                        }
                    }
                }

        subscriptions.add(subscription)
    }

    fun oauthLogin(token: String) {
        postState(AuthState.Loading)
        val subscription = gitHubRestClient.apiService.createOAuthAuthentication(token)
                .flatMap { doAuthorization(token) }
                .doOnSuccess { saveAccessToken(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { authorization, e ->
                    if (e == null) {
                        postState(AuthState.Success)
                    } else {
                        postState(AuthState.AuthError(e.message))
                    }
                }
        subscriptions.add(subscription)
    }

    private fun cleanUpAfterAuthorization() {
        // start deletion authorization on Github, because we don't need it anymore
        val cleanUpJob = if (!TextUtils.isEmpty(securityCode)) {
            gitHubRestClient.apiService.deleteAuthorization(basicAuth, securityCode!!, authorization.id.toString())
        } else {
            gitHubRestClient.apiService.deleteAuthorization(basicAuth, authorization.id.toString())
        }
        val task = cleanUpJob
                .onErrorReturn { Any() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        subscriptions.add(task)
    }

    private fun saveAccessToken(accessToken: AccessToken) {
        // save access token to settings
        val token = accessToken.accessToken
        appSettings.putAccessToken(token)
    }

    private fun doAuthorization(token: String): Single<AccessToken>? {
        val request = AccessTokenRequest()
        request.gitHubToken = token
        return travisRestClient.apiService.auth(request)
    }

    private fun doAuthorization(authorization: Authorization): Single<AccessToken> {
        this.authorization = authorization
        val request = AccessTokenRequest()
        request.gitHubToken = authorization.token
        return travisRestClient.apiService.auth(request)
    }

    private fun isTwoFactorAuthRequired(exception: HttpException): Boolean {
        val response = exception.response()

        var twoFactorAuthRequired = false
        for (header in response.headers().names()) {
            if (TWO_FACTOR_HEADER == header) {
                twoFactorAuthRequired = true
                break
            }
        }

        return response.code() == HttpURLConnection.HTTP_UNAUTHORIZED && twoFactorAuthRequired
    }

    /**
     * Prepares authorization request
     *
     * @return Authorization request
     */
    private fun prepareAuthorizationRequest(): AuthorizationRequest {
        val scopes = Arrays.asList("read:org", "user:email", "repo_deployment",
                "repo:status", "write:repo_hook", "repo")
        val note = String.format("varis_client_%1\$s", StringUtils.getRandomString())
        return AuthorizationRequest(scopes, note, null, null, null, null)
    }

    private fun postState(newState: AuthState) {
        authState.value = newState
    }
}
