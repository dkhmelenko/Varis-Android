package com.khmelenko.lab.travisclient.presenter;

import com.khmelenko.lab.travisclient.BuildConfig;
import com.khmelenko.lab.travisclient.dagger.DaggerTestComponent;
import com.khmelenko.lab.travisclient.dagger.TestComponent;
import com.khmelenko.lab.travisclient.network.retrofit.raw.RawClient;
import com.khmelenko.lab.travisclient.storage.AppSettings;
import com.khmelenko.lab.travisclient.storage.CacheStorage;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.view.BuildDetailsView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.client.Header;
import retrofit.client.Response;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link BuildsDetailsPresenter}
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TestBuildDetailsPresenter {

    @Inject
    TaskManager mTaskManager;

    @Inject
    EventBus mEventBus;

    @Inject
    CacheStorage mCacheStorage;

    @Inject
    RawClient mRawClient;

    private BuildsDetailsPresenter mBuildsDetailsPresenter;
    private BuildDetailsView mBuildDetailsView;

    @Before
    public void setup() {
        TestComponent component = DaggerTestComponent.builder().build();
        component.inject(this);

        mBuildsDetailsPresenter = spy(new BuildsDetailsPresenter(mEventBus, mTaskManager, mCacheStorage));
        mBuildDetailsView = mock(BuildDetailsView.class);
        mBuildsDetailsPresenter.attach(mBuildDetailsView);
    }

    @Test
    public void testStartLoadingLogNoToken() {
        final long jobId = 1L;

        final String expectedUrl = "https://sample.org";
        Response response = new Response(expectedUrl, 200, "", Collections.<Header>emptyList(), null);
        when(mRawClient.getApiService().getLog(String.valueOf(jobId))).thenReturn(response);

        mBuildsDetailsPresenter.startLoadingLog(jobId);
        verify(mTaskManager).getLogUrl(jobId);
        verify(mBuildDetailsView).setLogUrl(expectedUrl);
    }

    @Test
    public void testStartLoadingLogWithToken() {
        final long jobId = 1L;

        final String expectedUrl = "https://sample.org";
        Response response = new Response(expectedUrl, 200, "", Collections.<Header>emptyList(), null);
        final String accessToken = "test";
        final String authToken = "token " + accessToken;

        when(mRawClient.getApiService().getLog(authToken, String.valueOf(jobId))).thenReturn(response);

        AppSettings.putAccessToken(accessToken);
        mBuildsDetailsPresenter.startLoadingLog(jobId);
        verify(mTaskManager).getLogUrl(authToken, jobId);
        verify(mBuildDetailsView).setLogUrl(expectedUrl);
    }

    @Test
    public void testStartLoadingLogFailed() {
        final long jobId = 1L;

        final int errorCode = 401;
        final String errorMsg = "error";
        TaskError error = spy(new TaskError(errorCode, errorMsg));
        Response response = new Response("url", 200, "", Collections.<Header>emptyList(), null);
        error.setResponse(response);
        TaskException exception = spy(new TaskException(error));
        when(mRawClient.getApiService().getLog(String.valueOf(jobId))).thenThrow(exception);

        mBuildsDetailsPresenter.startLoadingLog(jobId);
        int loadLogInvocations = BuildsDetailsPresenter.LOAD_LOG_MAX_ATTEMPT + 1;
        verify(mTaskManager, times(loadLogInvocations)).getLogUrl(jobId);
        verify(mBuildDetailsView).showLoadingError(error.getMessage());
        verify(mBuildDetailsView).showLogError();
    }

    @Test
    public void testStartLoadingData() {

    }

    @Test
    public void testStartLoadingDataFailed() {

    }

    @Test
    public void testRestartBuild() {

    }

    @Test
    public void testRestartBuildFailed() {

    }

    @Test
    public void testCancelBuild() {

    }

    @Test
    public void testCancelBuildFailed() {

    }

    @Test
    public void testCanUserContributeToRepo() {

    }
}
