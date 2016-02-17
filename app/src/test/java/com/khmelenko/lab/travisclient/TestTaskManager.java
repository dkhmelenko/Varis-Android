package com.khmelenko.lab.travisclient;

import com.khmelenko.lab.travisclient.network.request.AccessTokenRequest;
import com.khmelenko.lab.travisclient.network.response.AccessToken;
import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.network.response.BuildHistory;
import com.khmelenko.lab.travisclient.network.response.Requests;
import com.khmelenko.lab.travisclient.network.retrofit.EmptyOutput;
import com.khmelenko.lab.travisclient.network.retrofit.GithubApiService;
import com.khmelenko.lab.travisclient.network.retrofit.RawApiService;
import com.khmelenko.lab.travisclient.network.retrofit.RestClient;
import com.khmelenko.lab.travisclient.network.retrofit.TravisApiService;
import com.khmelenko.lab.travisclient.task.LoaderAsyncTask;
import com.khmelenko.lab.travisclient.task.TaskHelper;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.task.travis.RestartBuildTask;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import de.greenrobot.event.EventBus;
import retrofit.client.Response;

import static org.mockito.Mockito.*;

/**
 * Testing TaskManager class
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
@PrepareForTest({RestClient.class, Response.class, TaskHelper.class, TaskManager.class, RestartBuildTask.class,
        BuildHistory.class, Requests.class, AccessTokenRequest.class, AccessToken.class})
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
        RestartBuildTask task = spy(new RestartBuildTask(0));
        TaskHelper taskHelper = new TaskHelper(mRestClient, mEventBus);

        LoaderAsyncTask.executeTask(task, taskHelper);
        verify(task).execute();
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

}
