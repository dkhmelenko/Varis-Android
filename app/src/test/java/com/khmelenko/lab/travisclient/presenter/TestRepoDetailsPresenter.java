package com.khmelenko.lab.travisclient.presenter;

import com.khmelenko.lab.travisclient.BuildConfig;
import com.khmelenko.lab.travisclient.dagger.DaggerTestComponent;
import com.khmelenko.lab.travisclient.dagger.TestComponent;
import com.khmelenko.lab.travisclient.network.response.Branch;
import com.khmelenko.lab.travisclient.network.response.Branches;
import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.network.response.BuildHistory;
import com.khmelenko.lab.travisclient.network.response.Commit;
import com.khmelenko.lab.travisclient.network.response.RequestData;
import com.khmelenko.lab.travisclient.network.response.Requests;
import com.khmelenko.lab.travisclient.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.view.RepoDetailsView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link RepoDetailsPresenter}
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TestRepoDetailsPresenter {

    @Inject
    TaskManager mTaskManager;

    @Inject
    EventBus mEventBus;

    @Inject
    TravisRestClient mTravisRestClient;

    private RepoDetailsPresenter mRepoDetailsPresenter;
    private RepoDetailsView mRepoDetailsView;

    @Before
    public void setup() {
        TestComponent component = DaggerTestComponent.builder().build();
        component.inject(this);

        mRepoDetailsPresenter = spy(new RepoDetailsPresenter(mTaskManager, mEventBus));
        mRepoDetailsView = mock(RepoDetailsView.class);
        mRepoDetailsPresenter.attach(mRepoDetailsView);
    }

    @Test
    public void testLoadBuildsHistory() {
        final String slug = "test";
        final List<Build> builds = new ArrayList<>();
        final List<Commit> commits = new ArrayList<>();
        final BuildHistory buildHistory = new BuildHistory();
        buildHistory.setBuilds(builds);
        buildHistory.setCommits(commits);

        when(mTravisRestClient.getApiService().getBuilds(slug)).thenReturn(buildHistory);

        mRepoDetailsPresenter.setRepoSlug(slug);
        mRepoDetailsPresenter.loadBuildsHistory();
        verify(mTaskManager).getBuildHistory(slug);
        verify(mRepoDetailsView).updateBuildHistory(buildHistory);
    }

    @Test
    public void testLoadBuildsHistoryFailed() {
        final String slug = "test";

        final int errorCode = 401;
        final String errorMsg = "error";
        TaskError error = spy(new TaskError(errorCode, errorMsg));
        TaskException exception = spy(new TaskException(error));
        when(mTravisRestClient.getApiService().getBuilds(slug)).thenThrow(exception);

        mRepoDetailsPresenter.setRepoSlug(slug);
        mRepoDetailsPresenter.loadBuildsHistory();
        verify(mTaskManager).getBuildHistory(slug);
        verify(mRepoDetailsView).showBuildHistoryLoadingError(error.getMessage());
    }

    @Test
    public void testLoadBranches() {
        final String slug = "test";
        final List<Branch> branch = new ArrayList<>();
        final List<Commit> commits = new ArrayList<>();
        final Branches branches = new Branches();
        branches.setBranches(branch);
        branches.setCommits(commits);

        when(mTravisRestClient.getApiService().getBranches(slug)).thenReturn(branches);

        mRepoDetailsPresenter.setRepoSlug(slug);
        mRepoDetailsPresenter.loadBranches();
        verify(mTaskManager).getBranches(slug);
        verify(mRepoDetailsView).updateBranches(branches);
    }

    @Test
    public void testLoadBranchesFailed() {
        final String slug = "test";

        final int errorCode = 401;
        final String errorMsg = "error";
        TaskError error = spy(new TaskError(errorCode, errorMsg));
        TaskException exception = spy(new TaskException(error));
        when(mTravisRestClient.getApiService().getBranches(slug)).thenThrow(exception);

        mRepoDetailsPresenter.setRepoSlug(slug);
        mRepoDetailsPresenter.loadBranches();
        verify(mTaskManager).getBranches(slug);
        verify(mRepoDetailsView).showBranchesLoadingError(error.getMessage());
    }

    @Test
    public void testLoadRequests() {
        final String slug = "test";
        final List<Build> builds = new ArrayList<>();
        final List<Commit> commits = new ArrayList<>();
        final List<RequestData> requestData = new ArrayList<>();
        final Requests requests = new Requests();
        requests.setCommits(commits);
        requests.setRequests(requestData);
        final BuildHistory buildHistory = new BuildHistory();
        buildHistory.setBuilds(builds);
        buildHistory.setCommits(commits);

        when(mTravisRestClient.getApiService().getRequests(slug)).thenReturn(requests);
        when(mTravisRestClient.getApiService().getPullRequestBuilds(slug)).thenReturn(buildHistory);

        mRepoDetailsPresenter.setRepoSlug(slug);
        mRepoDetailsPresenter.loadRequests();
        verify(mTaskManager).getRequests(slug);
        verify(mRepoDetailsView).updatePullRequests(requests);

    }

    @Test
    public void testLoadRequestsFailed() {
        final String slug = "test";

        final int errorCode = 401;
        final String errorMsg = "error";
        TaskError error = spy(new TaskError(errorCode, errorMsg));
        TaskException exception = spy(new TaskException(error));
        when(mTravisRestClient.getApiService().getRequests(slug)).thenThrow(exception);

        mRepoDetailsPresenter.setRepoSlug(slug);
        mRepoDetailsPresenter.loadRequests();
        verify(mTaskManager).getRequests(slug);
        verify(mRepoDetailsView).showPullRequestsLoadingError(error.getMessage());
    }

    @Test
    public void testSetRepoSlug() {
        final String slug = "test";
        mRepoDetailsPresenter.setRepoSlug(slug);
        assertEquals(slug, mRepoDetailsPresenter.getRepoSlug());
    }

    @Test
    public void testLoadData() {
        final String slug = "test";
        final List<Build> builds = new ArrayList<>();
        final List<Commit> commits = new ArrayList<>();
        final List<RequestData> requestData = new ArrayList<>();
        final Requests requests = new Requests();
        requests.setCommits(commits);
        requests.setRequests(requestData);
        final BuildHistory buildHistory = new BuildHistory();
        buildHistory.setBuilds(builds);
        buildHistory.setCommits(commits);
        final List<Branch> branch = new ArrayList<>();
        final Branches branches = new Branches();
        branches.setBranches(branch);
        branches.setCommits(commits);

        when(mTravisRestClient.getApiService().getBuilds(slug)).thenReturn(buildHistory);
        when(mTravisRestClient.getApiService().getBranches(slug)).thenReturn(branches);
        when(mTravisRestClient.getApiService().getRequests(slug)).thenReturn(requests);
        when(mTravisRestClient.getApiService().getPullRequestBuilds(slug)).thenReturn(buildHistory);

        mRepoDetailsPresenter.setRepoSlug(slug);
        mRepoDetailsPresenter.loadData();
        verify(mRepoDetailsPresenter).loadBuildsHistory();
        verify(mRepoDetailsPresenter).loadBranches();
        verify(mRepoDetailsPresenter).loadRequests();
    }
}
