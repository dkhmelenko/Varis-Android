package com.khmelenko.lab.varis.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.khmelenko.lab.varis.BuildConfig
import com.khmelenko.lab.varis.RxJavaRules
import com.khmelenko.lab.varis.auth.AuthState
import com.khmelenko.lab.varis.auth.AuthViewModel
import com.khmelenko.lab.varis.dagger.DaggerTestComponent
import com.khmelenko.lab.varis.network.request.AccessTokenRequest
import com.khmelenko.lab.varis.network.response.AccessToken
import com.khmelenko.lab.varis.network.response.Authorization
import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient
import com.khmelenko.lab.varis.network.retrofit.github.TWO_FACTOR_HEADER
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import com.khmelenko.lab.varis.storage.AppSettings
import com.khmelenko.lab.varis.util.EncryptionUtils
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Testing [AuthViewModel]
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class TestAuthViewModel {

    @get:Rule
    var rxJavaRules = RxJavaRules()

    @get:Rule
    var testRule: TestRule = InstantTaskExecutorRule()

    @Inject
    lateinit var travisRestClient: TravisRestClient

    @Inject
    lateinit var gitHubRestClient: GitHubRestClient

    @Inject
    lateinit var appSettings: AppSettings

    private lateinit var authViewModel: AuthViewModel

    private val stateObserver = mock<Observer<AuthState>>()

    @Before
    fun setup() {
        val component = DaggerTestComponent.builder().build()
        component.inject(this)

        authViewModel = spy(AuthViewModel(travisRestClient, gitHubRestClient, appSettings))
        authViewModel.state().observeForever(stateObserver)
    }

    @Test
    fun testUpdateServer() {
        val newEndpoint = "newEndpoint"
        authViewModel.updateServer(newEndpoint)

        verify(travisRestClient).updateTravisEndpoint(newEndpoint)
        verifyNoMoreInteractions(travisRestClient)
        verifyZeroInteractions(stateObserver)
    }

    @Test
    fun testLoginSuccess() {
        val login = "login"
        val password = "password"
        val auth = EncryptionUtils.generateBasicAuthorization(login, password)

        val gitHubToken = "gitHubToken"
        val authorization = Authorization()
        authorization.token = gitHubToken
        authorization.id = 1L
        whenever(gitHubRestClient.apiService.createNewAuthorization(any(), any()))
                .thenReturn(Single.just(authorization))
        whenever(gitHubRestClient.apiService.deleteAuthorization(any(), any())).thenReturn(Single.just(Any()))

        val accessToken = "token"
        val request = AccessTokenRequest()
        request.gitHubToken = gitHubToken
        val token = AccessToken(accessToken)
        whenever(travisRestClient.apiService.auth(request)).thenReturn(Single.just(token))

        authViewModel.login(login, password)

        verify(gitHubRestClient.apiService).createNewAuthorization(any(), any())
        verify(gitHubRestClient.apiService).deleteAuthorization(any(), any())

        verify(travisRestClient.apiService).auth(request)

        verify(stateObserver).onChanged(AuthState.Loading)
        verify(stateObserver).onChanged(AuthState.Success)
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testLoginGitHubAuthFailed() {
        val login = "login"
        val password = "password"
        val auth = EncryptionUtils.generateBasicAuthorization(login, password)

        val errorMsg = "error"
        val exception = Exception(errorMsg)
        whenever(gitHubRestClient.apiService.createNewAuthorization(eq(auth), any()))
                .thenReturn(Single.error(exception))

        authViewModel.login(login, password)

        verify(gitHubRestClient.apiService).createNewAuthorization(any(), any())

        verifyZeroInteractions(travisRestClient)

        verify(stateObserver).onChanged(AuthState.Loading)
        verify(stateObserver).onChanged(AuthState.AuthError(errorMsg))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testLoginAuthFailed() {
        val login = "login"
        val password = "password"

        val gitHubToken = "gitHubToken"
        val authorization = Authorization()
        authorization.token = gitHubToken
        authorization.id = 1L
        whenever(gitHubRestClient.apiService.createNewAuthorization(any(), any()))
                .thenReturn(Single.just(authorization))

        val request = AccessTokenRequest()
        request.gitHubToken = gitHubToken
        val errorMsg = "error"
        val exception = Exception(errorMsg)
        whenever(travisRestClient.apiService.auth(request)).thenReturn(Single.error(exception))

        authViewModel.login(login, password)

        verify(gitHubRestClient.apiService).createNewAuthorization(any(), any())
        verify(travisRestClient.apiService).auth(request)

        verify(stateObserver).onChanged(AuthState.Loading)
        verify(stateObserver).onChanged(AuthState.AuthError(errorMsg))
        verifyNoMoreInteractions(stateObserver)
    }

    @Test
    fun testTwoFactorAuth() {
        val login = "login"
        val password = "password"
        val auth = EncryptionUtils.generateBasicAuthorization(login, password)

        // rules for throwing a request for 2-factor auth
        val expectedUrl = "https://sample.org"
        val rawRequest = Request.Builder()
                .url(expectedUrl)
                .build()
        val rawResponse = Response.Builder()
                .request(rawRequest)
                .message("no body")
                .protocol(Protocol.HTTP_1_1)
                .code(401)
                .header(TWO_FACTOR_HEADER, "required")
                .build()

        val response = retrofit2.Response.error<ResponseBody>(ResponseBody.create(null, ""), rawResponse)
        val exception = HttpException(response)

        whenever(gitHubRestClient.apiService.createNewAuthorization(eq(auth), any()))
                .thenReturn(Single.error(exception))

        authViewModel.login(login, password)

        verify(stateObserver).onChanged(AuthState.Loading)
        verify(stateObserver).onChanged(AuthState.ShowTwoFactorAuth)

        // rules for handling 2-factor auth continuation
        val securityCode = "123456"
        val gitHubToken = "gitHubToken"
        val authorization = Authorization()
        authorization.token = gitHubToken
        authorization.id = 1L
        whenever(gitHubRestClient.apiService.createNewAuthorization(eq(auth), eq(securityCode), any()))
                .thenReturn(Single.just(authorization))
        whenever(gitHubRestClient.apiService.deleteAuthorization(auth, securityCode, authorization.id.toString()))
                .thenReturn(Single.just(Any()))

        val accessToken = "token"
        val request = AccessTokenRequest()
        request.gitHubToken = gitHubToken
        val token = AccessToken(accessToken)
        whenever(travisRestClient.apiService.auth(request)).thenReturn(Single.just(token))

        authViewModel.twoFactorAuth(securityCode)

        verify(stateObserver, times(2)).onChanged(AuthState.Loading)
        verify(stateObserver).onChanged(AuthState.Success)
        verifyNoMoreInteractions(stateObserver)
    }
}
