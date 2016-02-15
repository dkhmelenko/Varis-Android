package com.khmelenko.lab.travisclient;

import com.khmelenko.lab.travisclient.network.retrofit.EmptyOutput;
import com.khmelenko.lab.travisclient.network.retrofit.GithubApiService;
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

import static org.mockito.Mockito.*;

/**
 * Testing TaskManager class
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
@PrepareForTest({RestClient.class, TaskHelper.class, TaskManager.class, RestartBuildTask.class})
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

}
