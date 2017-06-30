package com.khmelenko.lab.varis.presenter;

import com.khmelenko.lab.varis.RxJavaRules;
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
import com.khmelenko.lab.varis.view.BuildDetailsView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link BuildsDetailsPresenter}
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class TestBuildDetailsPresenter {

    @Rule
    public RxJavaRules mRxJavaRules = new RxJavaRules();

    @Inject
    CacheStorage mCacheStorage;

    @Inject
    RawClient mRawClient;

    @Inject
    TravisRestClient mTravisRestClient;

    @Inject
    AppSettings mAppSettings;

    private BuildsDetailsPresenter mBuildsDetailsPresenter;
    private BuildDetailsView mBuildDetailsView;

    @Before
    public void setup() {
        TestComponent component = DaggerTestComponent.builder().build();
        component.inject(this);

        mBuildsDetailsPresenter = spy(new BuildsDetailsPresenter(mTravisRestClient, mRawClient, mCacheStorage, mAppSettings));
        mBuildDetailsView = mock(BuildDetailsView.class);
        mBuildsDetailsPresenter.attach(mBuildDetailsView);
    }

    @Test
    public void testStartLoadingLogNoToken() {
        final long jobId = 1L;

        final String expectedUrl = "https://sample.org";
        when(mRawClient.getApiService().getLog(String.valueOf(jobId))).thenReturn(Single.just(expectedUrl));
        when(mRawClient.getLogUrl(jobId)).thenReturn(expectedUrl);

        mBuildsDetailsPresenter.startLoadingLog(jobId);
        verify(mBuildDetailsView).setLogUrl(expectedUrl);
    }

    @Test
    public void testStartLoadingLogWithToken() {
        final long jobId = 1L;

        final String expectedUrl = "https://sample.org";
        final String accessToken = "test";
        final String authToken = "token " + accessToken;

        when(mRawClient.getApiService().getLog(authToken, String.valueOf(jobId))).thenReturn(Single.just(""));
        when(mRawClient.getLogUrl(any(Long.class))).thenReturn(expectedUrl);
        when(mAppSettings.getAccessToken()).thenReturn(accessToken);

        mBuildsDetailsPresenter.startLoadingLog(jobId);
        verify(mBuildDetailsView).setLogUrl(expectedUrl);
    }

    @Test
    public void testStartLoadingLogFailed() {
        final long jobId = 1L;

        final String errorMsg = "error";
        Exception exception = new Exception(errorMsg);
        when(mRawClient.getApiService().getLog(String.valueOf(jobId))).thenReturn(Single.error(exception));

        mBuildsDetailsPresenter.startLoadingLog(jobId);
        verify(mBuildDetailsView).showLoadingError(errorMsg);
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
        final String accessToken = "test";
        final String authToken = "token " + accessToken;

        when(mRawClient.getApiService().getLog(authToken, String.valueOf(job.getId()))).thenReturn(Single.just(""));
        when(mTravisRestClient.getApiService().getBuild(slug, buildId)).thenReturn(Single.just(buildDetails));
        when(mAppSettings.getAccessToken()).thenReturn(accessToken);

        mBuildsDetailsPresenter.startLoadingData(null, slug, buildId);
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

        when(mTravisRestClient.getApiService().getBuild(slug, buildId)).thenReturn(Single.just(buildDetails));

        mBuildsDetailsPresenter.startLoadingData(null, slug, buildId);
        verify(mBuildDetailsView).showProgress();
        verify(mBuildDetailsView).hideProgress();
        verify(mBuildDetailsView).updateBuildDetails(buildDetails);
        verify(mBuildDetailsView).showBuildJobs(jobs);
    }

    @Test
    public void testStartLoadingDataIntentUrl() throws Exception {
        final long buildId = 188971232L;
        final String slug = "dkhmelenko/Varis-Android";
        final String intentUrl = "https://travis-ci.org/dkhmelenko/Varis-Android/builds/188971232";

        when(mRawClient.singleRequest(intentUrl)).thenReturn(Single.just(intentResponse()));

        mBuildsDetailsPresenter.startLoadingData(intentUrl, slug, buildId);
    }

    @Test
    public void testStartLoadingDataFailed() {
        final long buildId = 1L;
        final String slug = "test";

        final String errorMsg = "error";
        Exception exception = new Exception(errorMsg);
        when(mTravisRestClient.getApiService().getBuild(slug, buildId)).thenReturn(Single.error(exception));

        mBuildsDetailsPresenter.startLoadingData(null, slug, buildId);
        verify(mBuildDetailsView).showProgress();
        verify(mBuildDetailsView).hideProgress();
        verify(mBuildDetailsView).updateBuildDetails(null);
        verify(mBuildDetailsView).showLoadingError(errorMsg);
    }

    @Test
    public void testStartLoadingDataIntentUrlFailed() throws Exception {
        final long buildId = 0L;
        final String slug = "dkhmelenko/Varis-Android";
        final String intentUrl = "https://travis-ci.org/dkhmelenko/Varis-Android/builds";

        Response response = intentResponse();
        final String errorMsg = "error";

        when(mRawClient.singleRequest(intentUrl)).thenReturn(Single.just(response));
        when(mTravisRestClient.getApiService().getBuild(any(String.class), any(Long.class)))
                .thenReturn(Single.error(new Exception(errorMsg)));

        mBuildsDetailsPresenter.startLoadingData(intentUrl, slug, buildId);
        verify(mBuildDetailsView).hideProgress();
        verify(mBuildDetailsView).updateBuildDetails(null);
        verify(mBuildDetailsView).showLoadingError(errorMsg);
    }

    @Test
    public void testRestartBuild() {
        BuildDetails buildDetails = new BuildDetails();

        when(mTravisRestClient.getApiService().restartBuild(any(Long.class), any(RequestBody.class)))
                .thenReturn(Single.just(new Object()));
        when(mTravisRestClient.getApiService().getBuild(any(String.class), any(Long.class)))
                .thenReturn(Single.just(buildDetails));

        mBuildsDetailsPresenter.restartBuild();

        // TODO Extend tests here
    }

    @Test
    public void testRestartBuildFailed() {
        final String errorMsg = "error";
        Exception exception = new Exception(errorMsg);

        when(mTravisRestClient.getApiService().restartBuild(any(Long.class), any(RequestBody.class)))
                .thenReturn(Single.error(exception));
        when(mTravisRestClient.getApiService().getBuild(any(String.class), any(Long.class)))
                .thenReturn(Single.error(exception));

        mBuildsDetailsPresenter.restartBuild();
        verify(mBuildDetailsView).showLoadingError(errorMsg);
    }

    @Test
    public void testCancelBuild() {
        BuildDetails buildDetails = new BuildDetails();

        when(mTravisRestClient.getApiService().cancelBuild(any(Long.class), any(RequestBody.class)))
                .thenReturn(Single.just(new Object()));
        when(mTravisRestClient.getApiService().getBuild(any(String.class), any(Long.class)))
                .thenReturn(Single.just(buildDetails));

        mBuildsDetailsPresenter.cancelBuild();

        // TODO Extend tests here
    }

    @Test
    public void testCancelBuildFailed() {
        final String errorMsg = "error";
        Exception exception = new Exception(errorMsg);

        when(mTravisRestClient.getApiService().cancelBuild(any(Long.class), any(RequestBody.class)))
                .thenReturn(Single.error(exception));
        when(mTravisRestClient.getApiService().getBuild(any(String.class), any(Long.class)))
                .thenReturn(Single.error(exception));

        mBuildsDetailsPresenter.cancelBuild();
        verify(mBuildDetailsView).showLoadingError(errorMsg);
    }

    @Test
    public void testCanUserContributeToRepoTrue() throws Exception {
        final long buildId = 188971232L;
        final String slug = "dkhmelenko/Varis-Android";
        final String intentUrl = "https://travis-ci.org/dkhmelenko/Varis-Android/builds/188971232";

        when(mRawClient.singleRequest(intentUrl)).thenReturn(Single.just(intentResponse()));
        String[] repos = {"Repo1", "Repo2", "Repo3", slug, "Repo4"};
        when(mCacheStorage.restoreRepos()).thenReturn(repos);

        mBuildsDetailsPresenter.startLoadingData(intentUrl, slug, buildId);
        boolean actual = mBuildsDetailsPresenter.canUserContributeToRepo();
        assertEquals(true, actual);
    }

    @Test
    public void testCanUserContributeToRepoFalse() throws Exception {
        final long buildId = 188971232L;
        final String slug = "dkhmelenko/Varis-Android";
        final String intentUrl = "https://travis-ci.org/dkhmelenko/Varis-Android/builds/188971232";

        when(mRawClient.singleRequest(intentUrl)).thenReturn(Single.just(intentResponse()));
        String[] repos = {"Repo1", "Repo2", "Repo3"};
        when(mCacheStorage.restoreRepos()).thenReturn(repos);

        mBuildsDetailsPresenter.startLoadingData(intentUrl, slug, buildId);
        boolean actual = mBuildsDetailsPresenter.canUserContributeToRepo();
        assertEquals(false, actual);
    }

    private Response intentResponse() {
        final String expectedUrl = "https://sample.org";
        Request request = new Request.Builder()
                .url(expectedUrl)
                .build();
        return new Response.Builder()
                .request(request)
                .message("no body")
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .build();
    }
}
