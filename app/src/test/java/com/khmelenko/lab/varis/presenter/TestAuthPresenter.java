package com.khmelenko.lab.varis.presenter;

import com.khmelenko.lab.varis.BuildConfig;
import com.khmelenko.lab.varis.dagger.DaggerTestComponent;
import com.khmelenko.lab.varis.dagger.TestComponent;
import com.khmelenko.lab.varis.network.request.AccessTokenRequest;
import com.khmelenko.lab.varis.network.request.AuthorizationRequest;
import com.khmelenko.lab.varis.network.response.AccessToken;
import com.khmelenko.lab.varis.network.response.Authorization;
import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient;
import com.khmelenko.lab.varis.network.retrofit.github.GithubApiService;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.task.TaskError;
import com.khmelenko.lab.varis.task.TaskException;
import com.khmelenko.lab.varis.task.TaskManager;
import com.khmelenko.lab.varis.util.EncryptionUtils;
import com.khmelenko.lab.varis.view.AuthView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.client.Header;
import retrofit.client.Response;

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
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TestAuthPresenter {

    @Inject
    TaskManager mTaskManager;

    @Inject
    EventBus mEventBus;

    @Inject
    TravisRestClient mTravisRestClient;

    @Inject
    GitHubRestClient mGitHubRestClient;

    private AuthPresenter mAuthPresenter;
    private AuthView mAuthView;

    @Before
    public void setup() {
        TestComponent component = DaggerTestComponent.builder().build();
        component.inject(this);

        mAuthPresenter = spy(new AuthPresenter(mTaskManager, mEventBus, mTravisRestClient));
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
                .thenReturn(authorization);

        final String accessToken = "token";
        AccessTokenRequest request = new AccessTokenRequest();
        request.setGithubToken(gitHubToken);
        AccessToken token = new AccessToken();
        token.setAccessToken(accessToken);
        when(mTravisRestClient.getApiService().auth(request)).thenReturn(token);

        when(mGitHubRestClient.getApiService().deleteAuthorization(auth, String.valueOf(authorization.getId())))
                .thenReturn(null);

        mAuthPresenter.login(login, password);

        verify(mTaskManager).createNewAuthorization(eq(auth), any(AuthorizationRequest.class));
        verify(mTaskManager).startAuth(gitHubToken);
        verify(mTaskManager).deleteAuthorization(auth, String.valueOf(authorization.getId()));
        verify(mAuthView).hideProgress();
        verify(mAuthView).finishView();
    }

    @Test
    public void testLoginGitHubAuthFailed() {
        final String login = "login";
        final String password = "password";
        String auth = EncryptionUtils.generateBasicAuthorization(login, password);

        TaskError taskError = new TaskError(500, "error");
        TaskException exception = new TaskException(taskError);
        when(mGitHubRestClient.getApiService().createNewAuthorization(eq(auth), any(AuthorizationRequest.class)))
                .thenThrow(exception);

        mAuthPresenter.login(login, password);

        verify(mTaskManager).createNewAuthorization(eq(auth), any(AuthorizationRequest.class));
        verify(mAuthView).hideProgress();
        verify(mAuthView).showErrorMessage(taskError.getMessage());
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
                .thenReturn(authorization);

        AccessTokenRequest request = new AccessTokenRequest();
        request.setGithubToken(gitHubToken);
        TaskError taskError = new TaskError(500, "error");
        TaskException exception = new TaskException(taskError);
        when(mTravisRestClient.getApiService().auth(request)).thenThrow(exception);

        mAuthPresenter.login(login, password);

        verify(mTaskManager).createNewAuthorization(eq(auth), any(AuthorizationRequest.class));
        verify(mTaskManager).startAuth(gitHubToken);
        verify(mAuthView).hideProgress();
        verify(mAuthView).showErrorMessage(taskError.getMessage());
    }

    @Test
    public void testTwoFactorAuth() {
        final String login = "login";
        final String password = "password";
        String auth = EncryptionUtils.generateBasicAuthorization(login, password);

        // rules for throwing a request for 2-factor auth
        Header header = new Header(GithubApiService.TWO_FACTOR_HEADER, "required");
        List<Header> headers = new ArrayList<>();
        headers.add(header);
        Response response = new Response("https://github.com", 401, "twoFactorAuth", headers, null);
        TaskError taskError = new TaskError(401, "twoFactorAuth");
        taskError.setResponse(response);
        TaskException exception = new TaskException(taskError);
        when(mGitHubRestClient.getApiService().createNewAuthorization(eq(auth), any(AuthorizationRequest.class)))
                .thenThrow(exception);

        mAuthPresenter.login(login, password);

        verify(mTaskManager).createNewAuthorization(eq(auth), any(AuthorizationRequest.class));
        verify(mAuthView).showTwoFactorAuth();

        // rules for handling 2-factor auth continuation
        final String securityCode = "123456";
        final String gitHubToken = "gitHubToken";
        Authorization authorization = new Authorization();
        authorization.setToken(gitHubToken);
        authorization.setId(1L);
        when(mGitHubRestClient.getApiService().createNewAuthorization(eq(auth), eq(securityCode), any(AuthorizationRequest.class)))
                .thenReturn(authorization);

        final String accessToken = "token";
        AccessTokenRequest request = new AccessTokenRequest();
        request.setGithubToken(gitHubToken);
        AccessToken token = new AccessToken();
        token.setAccessToken(accessToken);
        when(mTravisRestClient.getApiService().auth(request)).thenReturn(token);

        when(mGitHubRestClient.getApiService().deleteAuthorization(auth, String.valueOf(authorization.getId())))
                .thenReturn(null);

        mAuthPresenter.twoFactorAuth(securityCode);

        verify(mTaskManager).startAuth(gitHubToken);
        verify(mTaskManager).deleteAuthorization(auth, String.valueOf(authorization.getId()), securityCode);
        verify(mAuthView, times(2)).hideProgress();
        verify(mAuthView).finishView();
    }
}
