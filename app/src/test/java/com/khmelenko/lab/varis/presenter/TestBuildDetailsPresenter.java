package com.khmelenko.lab.varis.presenter;

import com.khmelenko.lab.varis.BuildConfig;
import com.khmelenko.lab.varis.dagger.DaggerTestComponent;
import com.khmelenko.lab.varis.dagger.TestComponent;
import com.khmelenko.lab.varis.network.response.Build;
import com.khmelenko.lab.varis.network.response.BuildDetails;
import com.khmelenko.lab.varis.network.response.Commit;
import com.khmelenko.lab.varis.network.response.Job;
import com.khmelenko.lab.varis.network.retrofit.raw.RawClient;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.storage.AppSettings;
import com.khmelenko.lab.varis.storage.CacheStorage;
import com.khmelenko.lab.varis.task.TaskError;
import com.khmelenko.lab.varis.task.TaskException;
import com.khmelenko.lab.varis.task.TaskManager;
import com.khmelenko.lab.varis.view.BuildDetailsView;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.mime.TypedOutput;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
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

    @Inject
    TravisRestClient mTravisRestClient;

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
    public void testStartLoadingDataSingleJob() {
        final long buildId = 1L;
        final String slug = "test";

        final Build build = new Build();
        final Commit commit = new Commit();
        final List<Job> jobs = new ArrayList<>();
        final Job job = new Job();
        jobs.add(job);
        BuildDetails buildDetails = new BuildDetails();
        buildDetails.setBuild(build);
        buildDetails.setCommit(commit);
        buildDetails.setJobs(jobs);

        final String expectedUrl = "https://sample.org";
        Response response = new Response(expectedUrl, 200, "", Collections.<Header>emptyList(), null);
        final String accessToken = "test";
        final String authToken = "token " + accessToken;

        AppSettings.putAccessToken("test");
        when(mRawClient.getApiService().getLog(authToken, String.valueOf(job.getId()))).thenReturn(response);
        when(mTravisRestClient.getApiService().getBuild(slug, buildId)).thenReturn(buildDetails);

        mBuildsDetailsPresenter.startLoadingData(null, slug, buildId);
        verify(mTaskManager).getBuildDetails(slug, buildId);
        verify(mBuildDetailsView).showProgress();
        verify(mBuildDetailsView).hideProgress();
        verify(mBuildDetailsView).updateBuildDetails(buildDetails);
        verify(mBuildDetailsView).showBuildLogs();
        verify(mBuildDetailsView).showAdditionalActionsForBuild(buildDetails);
    }

    @Test
    public void testStartLoadingDataManyJobs() {
        final long buildId = 1L;
        final String slug = "test";

        final Build build = new Build();
        final Commit commit = new Commit();
        final List<Job> jobs = new ArrayList<>();
        final Job job1 = new Job();
        jobs.add(job1);
        final Job job2 = new Job();
        jobs.add(job2);
        BuildDetails buildDetails = new BuildDetails();
        buildDetails.setBuild(build);
        buildDetails.setCommit(commit);
        buildDetails.setJobs(jobs);

        when(mTravisRestClient.getApiService().getBuild(slug, buildId)).thenReturn(buildDetails);

        mBuildsDetailsPresenter.startLoadingData(null, slug, buildId);
        verify(mTaskManager).getBuildDetails(slug, buildId);
        verify(mBuildDetailsView).showProgress();
        verify(mBuildDetailsView).hideProgress();
        verify(mBuildDetailsView).updateBuildDetails(buildDetails);
        verify(mBuildDetailsView).showBuildJobs(jobs);
    }

    @Test
    public void testStartLoadingDataIntentUrl() throws Exception {
        final long buildId = 188971232L;
        final String slug = "dkhmelenko/TravisClient-Android";
        final String intentUrl = "https://travis-ci.org/dkhmelenko/TravisClient-Android/builds/188971232";

        when(mRawClient.singleRequest(intentUrl)).thenReturn(intentResponse());

        mBuildsDetailsPresenter.startLoadingData(intentUrl, slug, buildId);
        verify(mTaskManager).intentUrl(intentUrl);
        verify(mTaskManager).getBuildDetails(slug, buildId);
    }

    @Test
    public void testStartLoadingDataFailed() {
        final long buildId = 1L;
        final String slug = "test";

        final int errorCode = 401;
        final String errorMsg = "error";
        TaskError error = spy(new TaskError(errorCode, errorMsg));
        Response response = new Response("url", 200, "", Collections.<Header>emptyList(), null);
        error.setResponse(response);
        TaskException exception = spy(new TaskException(error));
        when(mTravisRestClient.getApiService().getBuild(slug, buildId)).thenThrow(exception);

        mBuildsDetailsPresenter.startLoadingData(null, slug, buildId);
        verify(mTaskManager).getBuildDetails(slug, buildId);
        verify(mBuildDetailsView).showProgress();
        verify(mBuildDetailsView).hideProgress();
        verify(mBuildDetailsView).updateBuildDetails(null);
        verify(mBuildDetailsView).showLoadingError(error.getMessage());
    }

    @Test
    public void testStartLoadingDataIntentUrlFailed() throws Exception {
        final long buildId = 0L;
        final String slug = "dkhmelenko/TravisClient-Android";
        final String intentUrl = "https://travis-ci.org/dkhmelenko/TravisClient-Android/builds";

        final String expectedUrl = "https://sample.org";
        Request request = new Request.Builder()
                .url(expectedUrl)
                .build();
        com.squareup.okhttp.Response response = new com.squareup.okhttp.Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .build();

        final int errorCode = TaskError.NETWORK_ERROR;
        final String errorMsg = "";
        TaskError error = spy(new TaskError(errorCode, errorMsg));

        when(mRawClient.singleRequest(intentUrl)).thenReturn(response);

        mBuildsDetailsPresenter.startLoadingData(intentUrl, slug, buildId);
        verify(mTaskManager).intentUrl(intentUrl);
        verify(mBuildDetailsView).hideProgress();
        verify(mBuildDetailsView).updateBuildDetails(null);
        verify(mBuildDetailsView).showLoadingError(error.getMessage());
    }

    @Test
    public void testRestartBuild() {
        when(mTravisRestClient.getApiService().restartBuild(any(Long.class), any(TypedOutput.class)))
                .thenReturn(null);

        mBuildsDetailsPresenter.restartBuild();
        verify(mTaskManager).getBuildDetails(any(String.class), any(Long.class));
    }

    @Test
    public void testRestartBuildFailed() {
        final int errorCode = 401;
        final String errorMsg = "error";
        TaskError error = spy(new TaskError(errorCode, errorMsg));
        TaskException exception = spy(new TaskException(error));

        when(mTravisRestClient.getApiService().restartBuild(any(Long.class), any(TypedOutput.class)))
                .thenThrow(exception);

        mBuildsDetailsPresenter.restartBuild();
        verify(mTaskManager).getBuildDetails(any(String.class), any(Long.class));
        verify(mBuildDetailsView, times(2)).showLoadingError(error.getMessage());

    }

    @Test
    public void testCancelBuild() {
        when(mTravisRestClient.getApiService().cancelBuild(any(Long.class), any(TypedOutput.class)))
                .thenReturn(null);

        mBuildsDetailsPresenter.cancelBuild();
        verify(mTaskManager).getBuildDetails(any(String.class), any(Long.class));
    }

    @Test
    public void testCancelBuildFailed() {
        final int errorCode = 401;
        final String errorMsg = "error";
        TaskError error = spy(new TaskError(errorCode, errorMsg));
        TaskException exception = spy(new TaskException(error));

        when(mTravisRestClient.getApiService().cancelBuild(any(Long.class), any(TypedOutput.class)))
                .thenThrow(exception);

        mBuildsDetailsPresenter.cancelBuild();
        verify(mTaskManager).getBuildDetails(any(String.class), any(Long.class));
        verify(mBuildDetailsView, times(2)).showLoadingError(error.getMessage());

    }

    @Test
    public void testCanUserContributeToRepoTrue() throws Exception {
        final long buildId = 188971232L;
        final String slug = "dkhmelenko/TravisClient-Android";
        final String intentUrl = "https://travis-ci.org/dkhmelenko/TravisClient-Android/builds/188971232";

        when(mRawClient.singleRequest(intentUrl)).thenReturn(intentResponse());
        String[] repos = {"Repo1", "Repo2", "Repo3", slug, "Repo4"};
        when(mCacheStorage.restoreRepos()).thenReturn(repos);

        mBuildsDetailsPresenter.startLoadingData(intentUrl, slug, buildId);
        boolean actual = mBuildsDetailsPresenter.canUserContributeToRepo();
        assertEquals(true, actual);
    }

    @Test
    public void testCanUserContributeToRepoFalse() throws Exception {
        final long buildId = 188971232L;
        final String slug = "dkhmelenko/TravisClient-Android";
        final String intentUrl = "https://travis-ci.org/dkhmelenko/TravisClient-Android/builds/188971232";

        when(mRawClient.singleRequest(intentUrl)).thenReturn(intentResponse());
        String[] repos = {"Repo1", "Repo2", "Repo3"};
        when(mCacheStorage.restoreRepos()).thenReturn(repos);

        mBuildsDetailsPresenter.startLoadingData(intentUrl, slug, buildId);
        boolean actual = mBuildsDetailsPresenter.canUserContributeToRepo();
        assertEquals(false, actual);
    }

    private com.squareup.okhttp.Response intentResponse() {
        final String expectedUrl = "https://sample.org";
        Request request = new Request.Builder()
                .url(expectedUrl)
                .build();
        return new com.squareup.okhttp.Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .build();
    }
}
