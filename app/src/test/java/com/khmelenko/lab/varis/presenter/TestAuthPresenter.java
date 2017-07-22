package com.khmelenko.lab.varis.presenter;

import com.khmelenko.lab.varis.BuildConfig;
import com.khmelenko.lab.varis.RxJavaRules;
import com.khmelenko.lab.varis.dagger.DaggerTestComponent;
import com.khmelenko.lab.varis.dagger.TestComponent;
import com.khmelenko.lab.varis.network.request.AccessTokenRequest;
import com.khmelenko.lab.varis.network.request.AuthorizationRequest;
import com.khmelenko.lab.varis.network.response.AccessToken;
import com.khmelenko.lab.varis.network.response.Authorization;
import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient;
import com.khmelenko.lab.varis.network.retrofit.github.GithubApiService;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.storage.AppSettings;
import com.khmelenko.lab.varis.util.EncryptionUtils;
import com.khmelenko.lab.varis.view.AuthView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import io.reactivex.Single;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link AuthPresenter}
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TestAuthPresenter {

    @Rule
    public RxJavaRules mRxJavaRules = new RxJavaRules();

    @Inject
    TravisRestClient mTravisRestClient;

    @Inject
    GitHubRestClient mGitHubRestClient;

    @Inject
    AppSettings mAppSettings;

    private AuthPresenter mAuthPresenter;
    private AuthView mAuthView;

    @Before
    public void setup() {
        TestComponent component = DaggerTestComponent.builder().build();
        component.inject(this);

        mAuthPresenter = spy(new AuthPresenter(mTravisRestClient, mGitHubRestClient, mAppSettings));
        mAuthView = mock(AuthView.class);
        mAuthPresenter.attach(mAuthView);
    }

    @Test
    public void testUpdateServer() {
        final String newEndpoint = "newEndpoint";
        mAuthPresenter.updateServer(newEndpoint);

        verify(mTravisRestClient).updateTravisEndpoint(newEndpoint);
    }

    @Test
    public void testLoginSuccess() {
        final String login = "login";
        final String password = "password";
        String auth = EncryptionUtils.generateBasicAuthorization(login, password);

        final String gitHubToken = "gitHubToken";
        Authorization authorization = new Authorization();
        authorization.setToken(gitHubToken);
        authorization.setId(1L);
        when(mGitHubRestClient.getApiService().createNewAuthorization(eq(auth), any(AuthorizationRequest.class)))
                .thenReturn(Single.just(authorization));

        final String accessToken = "token";
        AccessTokenRequest request = new AccessTokenRequest();
        request.setGithubToken(gitHubToken);
        AccessToken token = new AccessToken();
        token.setAccessToken(accessToken);
        when(mTravisRestClient.getApiService().auth(request)).thenReturn(Single.just(token));

        when(mGitHubRestClient.getApiService().deleteAuthorization(auth, String.valueOf(authorization.getId())))
                .thenReturn(null);

        mAuthPresenter.login(login, password);

        verify(mAuthView).hideProgress();
        verify(mAuthView).finishView();
    }

    @Test
    public void testLoginGitHubAuthFailed() {
        final String login = "login";
        final String password = "password";
        String auth = EncryptionUtils.generateBasicAuthorization(login, password);

        final String errorMsg = "error";
        Exception exception = new Exception(errorMsg);
        when(mGitHubRestClient.getApiService().createNewAuthorization(eq(auth), any(AuthorizationRequest.class)))
                .thenReturn(Single.error(exception));

        mAuthPresenter.login(login, password);

        verify(mAuthView).hideProgress();
        verify(mAuthView).showErrorMessage(errorMsg);
    }

    @Test
    public void testLoginAuthFailed() {
        final String login = "login";
        final String password = "password";
        String auth = EncryptionUtils.generateBasicAuthorization(login, password);

        final String gitHubToken = "gitHubToken";
        Authorization authorization = new Authorization();
        authorization.setToken(gitHubToken);
        authorization.setId(1L);
        when(mGitHubRestClient.getApiService().createNewAuthorization(eq(auth), any(AuthorizationRequest.class)))
                .thenReturn(Single.just(authorization));

        AccessTokenRequest request = new AccessTokenRequest();
        request.setGithubToken(gitHubToken);
        final String errorMsg = "error";
        Exception exception = new Exception(errorMsg);
        when(mTravisRestClient.getApiService().auth(request)).thenReturn(Single.error(exception));

        mAuthPresenter.login(login, password);

        verify(mAuthView).hideProgress();
        verify(mAuthView).showErrorMessage(errorMsg);
    }

    @Test
    public void testTwoFactorAuth() {
        final String login = "login";
        final String password = "password";
        String auth = EncryptionUtils.generateBasicAuthorization(login, password);

        // rules for throwing a request for 2-factor auth
        final String expectedUrl = "https://sample.org";
        Request rawRequest = new Request.Builder()
                .url(expectedUrl)
                .build();
        okhttp3.Response rawResponse = new okhttp3.Response.Builder()
                .request(rawRequest)
                .message("no body")
                .protocol(Protocol.HTTP_1_1)
                .code(401)
                .header(GithubApiService.TWO_FACTOR_HEADER, "required")
                .build();

        Response response = Response.error(ResponseBody.create(null, ""), rawResponse);
        HttpException exception = new HttpException(response);

        when(mGitHubRestClient.getApiService().createNewAuthorization(eq(auth), any(AuthorizationRequest.class)))
                .thenReturn(Single.error(exception));

        mAuthPresenter.login(login, password);

        verify(mAuthView).showTwoFactorAuth();

        // rules for handling 2-factor auth continuation
        final String securityCode = "123456";
        final String gitHubToken = "gitHubToken";
        Authorization authorization = new Authorization();
        authorization.setToken(gitHubToken);
        authorization.setId(1L);
        when(mGitHubRestClient.getApiService().createNewAuthorization(eq(auth), eq(securityCode), any(AuthorizationRequest.class)))
                .thenReturn(Single.just(authorization));

        final String accessToken = "token";
        AccessTokenRequest request = new AccessTokenRequest();
        request.setGithubToken(gitHubToken);
        AccessToken token = new AccessToken();
        token.setAccessToken(accessToken);
        when(mTravisRestClient.getApiService().auth(request)).thenReturn(Single.just(token));

        when(mGitHubRestClient.getApiService().deleteAuthorization(auth, String.valueOf(authorization.getId())))
                .thenReturn(null);

        mAuthPresenter.twoFactorAuth(securityCode);

        verify(mAuthView, times(2)).hideProgress();
        verify(mAuthView).finishView();
    }
}
