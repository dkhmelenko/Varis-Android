package com.khmelenko.lab.varis;

import com.khmelenko.lab.varis.dagger.DaggerTestComponent;
import com.khmelenko.lab.varis.dagger.TestComponent;
import com.khmelenko.lab.varis.network.request.AccessTokenRequest;
import com.khmelenko.lab.varis.network.request.AuthorizationRequest;
import com.khmelenko.lab.varis.network.response.AccessToken;
import com.khmelenko.lab.varis.network.response.BuildHistory;
import com.khmelenko.lab.varis.network.response.Repo;
import com.khmelenko.lab.varis.network.response.Requests;
import com.khmelenko.lab.varis.task.LoaderAsyncTask;
import com.khmelenko.lab.varis.task.Task;
import com.khmelenko.lab.varis.task.TaskError;
import com.khmelenko.lab.varis.task.TaskException;
import com.khmelenko.lab.varis.task.TaskHelper;
import com.khmelenko.lab.varis.task.TaskManager;
import com.khmelenko.lab.varis.task.github.CreateAuthorizationTask;
import com.khmelenko.lab.varis.task.github.DeleteAuthorizationTask;
import com.khmelenko.lab.varis.task.travis.AuthTask;
import com.khmelenko.lab.varis.task.travis.BranchesTask;
import com.khmelenko.lab.varis.task.travis.BuildDetailsTask;
import com.khmelenko.lab.varis.task.travis.BuildHistoryTask;
import com.khmelenko.lab.varis.task.travis.CancelBuildTask;
import com.khmelenko.lab.varis.task.travis.FindRepoTask;
import com.khmelenko.lab.varis.task.travis.LogTask;
import com.khmelenko.lab.varis.task.travis.RepoTask;
import com.khmelenko.lab.varis.task.travis.RequestsTask;
import com.khmelenko.lab.varis.task.travis.RestartBuildTask;
import com.khmelenko.lab.varis.task.travis.UserReposTask;
import com.khmelenko.lab.varis.task.travis.UserTask;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import retrofit.client.Header;
import retrofit.client.Response;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing TaskManager class
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TestTaskManager {

    @Inject
    TaskManager mTaskManager;

    @Inject
    TravisRestClient mTravisRestClient;
    @Inject
    GitHubRestClient mGitHubRestClient;
    @Inject
    RawClient mRawClient;
    @Inject
    TaskHelper mTaskHelper;

    @Before
    public void setupMock() {
        TestComponent component = DaggerTestComponent.builder().build();
        component.inject(this);
    }

    @Test
    public void testTaskExecution() {
        final String request = "test";
        FindRepoTask task = spy(new FindRepoTask(request));
        List<Repo> result = spy(new ArrayList<Repo>());
        when(mTravisRestClient.getApiService().getRepos(request)).thenReturn(result);

        LoaderAsyncTask.executeTask(task, mTaskHelper);
        verify(task).execute();
        verify(task).onSuccess(result);
    }

    private void testTaskFailed(Task task) {
        final int errorCode = 401;
        final String errorMsg = "error";

        task.setHelper(mTaskHelper);
        List<Header> headers = new ArrayList<>();
        Response response = new Response("url", errorCode, errorMsg, headers, null);
        TaskError error = spy(new TaskError(errorCode, errorMsg));
        TaskException exception = spy(new TaskException(error));
        when(task.execute()).thenThrow(exception);
        when(error.getResponse()).thenReturn(response);

        ArgumentCaptor<TaskError> argument = ArgumentCaptor.forClass(TaskError.class);
        LoaderAsyncTask.executeTask(task, mTaskHelper);
        verify(task).onFail(argument.capture());

        assertEquals(errorCode, argument.getValue().getCode());
        assertEquals(errorMsg, argument.getValue().getMessage());
    }

    @Test
    public void testRestartBuildTask() {
        mTaskManager.restartBuild(anyLong());
        verify(mTravisRestClient.getApiService()).restartBuild(anyLong(), eq(EmptyOutput.INSTANCE));

        final int buildId = 0;
        RestartBuildTask task = spy(new RestartBuildTask(buildId));
        testTaskFailed(task);
    }

    @Test
    public void testCancelBuildTask() {
        mTaskManager.cancelBuild(anyLong());
        verify(mTravisRestClient.getApiService()).cancelBuild(anyLong(), eq(EmptyOutput.INSTANCE));

        final int buildId = 0;
        CancelBuildTask task = spy(new CancelBuildTask(buildId));
        testTaskFailed(task);
    }

    @Test
    public void testSearchRepo() {
        mTaskManager.findRepos("test");
        verify(mTravisRestClient.getApiService()).getRepos("test");

        mTaskManager.findRepos(eq(""));
        verify(mTravisRestClient.getApiService()).getRepos();

        final String testRepo = "testRepo";
        FindRepoTask task = spy(new FindRepoTask(testRepo));
        testTaskFailed(task);
    }

    @Test
    public void testGetRepo() {
        final String testRepo = "testRepo";
        mTaskManager.getRepo(testRepo);
        verify(mTravisRestClient.getApiService()).getRepo(testRepo);

        RepoTask task = spy(new RepoTask(testRepo));
        testTaskFailed(task);
    }

    @Test
    public void testGetUserRepos() {
        mTaskManager.userRepos(anyString());
        verify(mTravisRestClient.getApiService()).getUserRepos(anyString());

        final String testRepo = "testRepo";
        UserReposTask task = spy(new UserReposTask(testRepo));
        testTaskFailed(task);
    }

    @Test
    public void testGetBuildHistory() {
        mTaskManager.getBuildHistory("test");
        verify(mTravisRestClient.getApiService()).getBuilds("test");

        final String testRepo = "testRepo";
        BuildHistoryTask task = spy(new BuildHistoryTask(testRepo));
        testTaskFailed(task);
    }

    @Test
    public void testGetBranches() {
        mTaskManager.getBranches("test");
        verify(mTravisRestClient.getApiService()).getBranches("test");

        final String testRepo = "testRepo";
        BranchesTask task = spy(new BranchesTask(testRepo));
        testTaskFailed(task);
    }

    @Test
    public void testGetRequests() {
        when(mTravisRestClient.getApiService().getRequests(anyString())).thenReturn(spy(new Requests()));
        when(mTravisRestClient.getApiService().getPullRequestBuilds(anyString())).thenReturn(mock(BuildHistory.class));

        mTaskManager.getRequests("test");
        verify(mTravisRestClient.getApiService()).getRequests("test");
        verify(mTravisRestClient.getApiService()).getPullRequestBuilds("test");

        final String testRepo = "testRepo";
        RequestsTask task = spy(new RequestsTask(testRepo));
        testTaskFailed(task);
    }

    @Test
    public void testGetBuildDetails() {
        final String testRepo = "testRepo";
        final int buildId = 0;

        mTaskManager.getBuildDetails(testRepo, buildId);
        verify(mTravisRestClient.getApiService()).getBuild(testRepo, buildId);

        BuildDetailsTask task = spy(new BuildDetailsTask(testRepo, buildId));
        testTaskFailed(task);
    }

    @Test
    public void testUser() {
        mTaskManager.getUser();
        verify(mTravisRestClient.getApiService()).getUser();

        UserTask task = spy(new UserTask());
        testTaskFailed(task);
    }

    @Test
    public void testAuth() {
        final String auth = "test";
        AccessTokenRequest request = new AccessTokenRequest();
        request.setGithubToken(auth);
        AccessToken token = mock(AccessToken.class);
        when(token.getAccessToken()).thenReturn(auth);
        when(mTravisRestClient.getApiService().auth(request)).thenReturn(token);

        mTaskManager.startAuth(auth);
        verify(mTravisRestClient.getApiService()).auth(request);

        final String accessToken = "token";
        AuthTask task = spy(new AuthTask(accessToken));
        testTaskFailed(task);
    }

    @Test
    public void testGetLogs() {
        final String auth = "test";
        final long jobId = 0;

        Response response = new Response("url", 200, "", Collections.<Header>emptyList(), null);
        when(mRawClient.getApiService().getLog(anyString())).thenReturn(response);
        when(mRawClient.getApiService().getLog(anyString(), anyString())).thenReturn(response);

        mTaskManager.getLogUrl(anyLong());
        verify(mRawClient.getApiService()).getLog(anyString());

        mTaskManager.getLogUrl(auth, jobId);
        verify(mRawClient.getApiService()).getLog(anyString(), anyString());

        LogTask task = spy(new LogTask(auth, jobId));
        testTaskFailed(task);
    }

    @Test
    public void testGithubCreateNewAuth() {
        String authToken = "test";
        AuthorizationRequest request = mock(AuthorizationRequest.class);
        mTaskManager.createNewAuthorization(authToken, request);
        verify(mGitHubRestClient.getApiService()).createNewAuthorization(authToken, request);

        mTaskManager.createNewAuthorization(authToken, request, authToken);
        verify(mGitHubRestClient.getApiService()).createNewAuthorization(authToken, authToken, request);

        List<String> scopes = new ArrayList<>();
        AuthorizationRequest authRequest = spy(new AuthorizationRequest(scopes, ""));
        CreateAuthorizationTask task = spy(new CreateAuthorizationTask(authToken, authRequest));
        testTaskFailed(task);
    }

    @Test
    public void testGithubDeleteAuth() {
        String auth = "test";
        mTaskManager.deleteAuthorization(auth, auth);
        verify(mGitHubRestClient.getApiService()).deleteAuthorization(auth, auth);

        mTaskManager.deleteAuthorization(auth, auth, auth);
        verify(mGitHubRestClient.getApiService()).deleteAuthorization(auth, auth, auth);

        DeleteAuthorizationTask task = spy(new DeleteAuthorizationTask(auth, auth));
        testTaskFailed(task);
    }

    @Test
    public void testSingleRequest() throws IOException {
        String anyUrl = "https://google.com.ua";
        Request request = new Request.Builder()
                .url(anyUrl)
                .build();
        com.squareup.okhttp.Response response = new com.squareup.okhttp.Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .build();
        when(mRawClient.singleRequest(anyUrl)).thenReturn(response);

        mTaskManager.intentUrl(anyUrl);
        verify(mRawClient).singleRequest(anyUrl);
    }

}
