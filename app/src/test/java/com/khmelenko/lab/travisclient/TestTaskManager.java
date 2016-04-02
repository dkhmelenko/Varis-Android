package com.khmelenko.lab.travisclient;

import com.khmelenko.lab.travisclient.network.request.AccessTokenRequest;
import com.khmelenko.lab.travisclient.network.request.AuthorizationRequest;
import com.khmelenko.lab.travisclient.network.response.AccessToken;
import com.khmelenko.lab.travisclient.network.response.BuildHistory;
import com.khmelenko.lab.travisclient.network.response.Repo;
import com.khmelenko.lab.travisclient.network.response.Requests;
import com.khmelenko.lab.travisclient.network.retrofit.EmptyOutput;
import com.khmelenko.lab.travisclient.network.retrofit.GithubApiService;
import com.khmelenko.lab.travisclient.network.retrofit.RawApiService;
import com.khmelenko.lab.travisclient.network.retrofit.RestClient;
import com.khmelenko.lab.travisclient.network.retrofit.TravisApiService;
import com.khmelenko.lab.travisclient.task.LoaderAsyncTask;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;
import com.khmelenko.lab.travisclient.task.TaskHelper;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.task.travis.FindRepoTask;
import com.khmelenko.lab.travisclient.task.travis.RestartBuildTask;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.client.Response;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Testing TaskManager class
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
@PrepareForTest({RestClient.class, Response.class, com.squareup.okhttp.Response.class, TaskHelper.class, TaskManager.class, TaskError.class,
        RestartBuildTask.class, BuildHistory.class, Requests.class, AccessTokenRequest.class, AccessToken.class,
        AuthorizationRequest.class, FindRepoTask.class})
public class TestTaskManager {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private TaskManager mTaskManager;
    private TaskHelper mTaskHelper;
    private EventBus mEventBus;
    private RestClient mRestClient;

    @Before
    public void setupMock() {
        mRestClient = mock(RestClient.class);
        TravisApiService apiService = mock(TravisApiService.class);
        when(mRestClient.getApiService()).thenReturn(apiService);
        GithubApiService githubApiService = mock(GithubApiService.class);
        when(mRestClient.getGithubApiService()).thenReturn(githubApiService);
        RawApiService rawApiService = mock(RawApiService.class);
        when(mRestClient.getRawApiService()).thenReturn(rawApiService);

        mEventBus = mock(EventBus.class);

        mTaskHelper = spy(new TaskHelper(mRestClient, mEventBus));
        mTaskManager = spy(new TaskManager(mTaskHelper));
    }

    @Test
    public void testTaskExecution() {
        final String request = "test";
        FindRepoTask task = spy(new FindRepoTask(request));
        List<Repo> result = spy(new ArrayList<Repo>());
        when(mRestClient.getApiService().getRepos(request)).thenReturn(result);

        LoaderAsyncTask.executeTask(task, mTaskHelper);
        verify(task).execute();
        verify(task).onSuccess(result);
    }

    @Test
    public void testTaskFailed() {
        final int errorCode = 401;
        final String errorMsg = "error";

        RestartBuildTask task = spy(new RestartBuildTask(0));
        task.setHelper(mTaskHelper);
        TaskError error = spy(new TaskError(errorCode, errorMsg));
        TaskException exception = spy(new TaskException(error));
        when(task.execute()).thenThrow(exception);

        ArgumentCaptor<TaskError> argument = ArgumentCaptor.forClass(TaskError.class);
        LoaderAsyncTask.executeTask(task, mTaskHelper);
        verify(task).onFail(argument.capture());

        assertEquals(errorCode, argument.getValue().getCode());
        assertEquals(errorMsg, argument.getValue().getMessage());
    }

    @Test
    public void testRestartBuildTask() {
        mTaskManager.restartBuild(anyLong());
        verify(mRestClient.getApiService()).restartBuild(anyLong(), eq(EmptyOutput.INSTANCE));
    }

    @Test
    public void testCancelBuildTask() {
        mTaskManager.cancelBuild(anyLong());
        verify(mRestClient.getApiService()).cancelBuild(anyLong(), eq(EmptyOutput.INSTANCE));
    }

    @Test
    public void testSearchRepo() {
        mTaskManager.findRepos("test");
        verify(mRestClient.getApiService()).getRepos("test");

        mTaskManager.findRepos(eq(""));
        verify(mRestClient.getApiService()).getRepos();
    }

    @Test
    public void testGetUserRepos() {
        mTaskManager.userRepos(anyString());
        verify(mRestClient.getApiService()).getUserRepos(anyString());
    }

    @Test
    public void testGetBuildHistory() {
        mTaskManager.getBuildHistory("test");
        verify(mRestClient.getApiService()).getBuilds("test");
    }

    @Test
    public void testGetBranches() {
        mTaskManager.getBranches("test");
        verify(mRestClient.getApiService()).getBranches("test");
    }

    @Test
    public void testGetRequests() {
        when(mRestClient.getApiService().getRequests(anyString())).thenReturn(spy(new Requests()));
        when(mRestClient.getApiService().getPullRequestBuilds(anyString())).thenReturn(mock(BuildHistory.class));

        mTaskManager.getRequests("test");
        verify(mRestClient.getApiService()).getRequests("test");
        verify(mRestClient.getApiService()).getPullRequestBuilds("test");
    }

    @Test
    public void testGetBuildDetails() {
        mTaskManager.getBuildDetails("test", 0);
        verify(mRestClient.getApiService()).getBuild("test", 0);
    }

    @Test
    public void testUser() {
        mTaskManager.getUser();
        verify(mRestClient.getApiService()).getUser();
    }

    @Test
    public void testAuth() {
        final String auth = "test";
        AccessTokenRequest request = new AccessTokenRequest();
        request.setGithubToken(auth);
        AccessToken token = mock(AccessToken.class);
        when(token.getAccessToken()).thenReturn(auth);
        when(mRestClient.getApiService().auth(request)).thenReturn(token);

        mTaskManager.startAuth(auth);
        verify(mRestClient.getApiService()).auth(request);
    }

    @Test
    public void testGetLogs() {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(200);
        when(response.getUrl()).thenReturn("url");
        when(mRestClient.getRawApiService().getLog(anyString())).thenReturn(response);
        when(mRestClient.getRawApiService().getLog(anyString(), anyString())).thenReturn(response);

        mTaskManager.getLogUrl(anyLong());
        verify(mRestClient.getRawApiService()).getLog(anyString());

        mTaskManager.getLogUrl("test", 0);
        verify(mRestClient.getRawApiService()).getLog(anyString(), anyString());
    }

    @Test
    public void testGithubCreateNewAuth() {
        String authToken = "test";
        AuthorizationRequest request = mock(AuthorizationRequest.class);
        mTaskManager.createNewAuthorization(authToken, request);
        verify(mRestClient.getGithubApiService()).createNewAuthorization(authToken, request);

        mTaskManager.createNewAuthorization(authToken, request, authToken);
        verify(mRestClient.getGithubApiService()).createNewAuthorization(authToken, authToken, request);
    }

    @Test
    public void testGithubDeleteAuth() {
        String auth = "test";
        mTaskManager.deleteAuthorization(auth, auth);
        verify(mRestClient.getGithubApiService()).deleteAuthorization(auth, auth);

        mTaskManager.deleteAuthorization(auth, auth, auth);
        verify(mRestClient.getGithubApiService()).deleteAuthorization(auth, auth, auth);
    }

    @Test
    public void testSingleRequest() throws IOException {
        String anyUrl = "https://google.com.ua";
        com.squareup.okhttp.Response response = mock(com.squareup.okhttp.Response.class);
        when(mRestClient.singleRequest(anyUrl)).thenReturn(response);

        mTaskManager.intentUrl(anyUrl);
        verify(mRestClient).singleRequest(anyUrl);
    }

}
