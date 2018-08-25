package com.khmelenko.lab.varis.viewmodel;

import com.khmelenko.lab.varis.repodetails.RepoDetailsViewModel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Testing {@link RepoDetailsViewModel}
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class TestRepoDetailsPresenter {

    // TODO
//    @Rule
//    public RxJavaRules mRxJavaRules = new RxJavaRules();
//
//    @Inject
//    TravisRestClient mTravisRestClient;
//
//    private RepoDetailsViewModel mRepoDetailsPresenter;
//    private RepoDetailsView mRepoDetailsView;
//
//    @Before
//    public void setup() {
//        TestComponent component = DaggerTestComponent.builder().build();
//        component.inject(this);
//
//        mRepoDetailsPresenter = spy(new RepoDetailsViewModel(mTravisRestClient));
//        mRepoDetailsView = mock(RepoDetailsView.class);
//        mRepoDetailsPresenter.attach(mRepoDetailsView);
//    }
//
//    @Test
//    public void testLoadBuildsHistory() {
//        final String slug = "test";
//        final List<Build> builds = new ArrayList<>();
//        final List<Commit> commits = new ArrayList<>();
//        final BuildHistory buildHistory = new BuildHistory();
//        buildHistory.setBuilds(builds);
//        buildHistory.setCommits(commits);
//
//        when(mTravisRestClient.getApiService().getBuilds(slug)).thenReturn(Single.just(buildHistory));
//
//        mRepoDetailsPresenter.setRepoSlug(slug);
//        mRepoDetailsPresenter.loadBuildsHistory();
//        verify(mRepoDetailsView).updateBuildHistory(buildHistory);
//    }
//
//    @Test
//    public void testLoadBuildsHistoryFailed() {
//        final String slug = "test";
//
//        final String errorMsg = "error";
//        Exception exception = new Exception(errorMsg);
//        when(mTravisRestClient.getApiService().getBuilds(slug)).thenReturn(Single.error(exception));
//
//        mRepoDetailsPresenter.setRepoSlug(slug);
//        mRepoDetailsPresenter.loadBuildsHistory();
//        verify(mRepoDetailsView).showBuildHistoryLoadingError(errorMsg);
//    }
//
//    @Test
//    public void testLoadBranches() {
//        final String slug = "test";
//        final List<Branch> branch = new ArrayList<>();
//        final List<Commit> commits = new ArrayList<>();
//        final Branches branches = new Branches();
//        branches.setBranches(branch);
//        branches.setCommits(commits);
//
//        when(mTravisRestClient.getApiService().getBranches(slug)).thenReturn(Single.just(branches));
//
//        mRepoDetailsPresenter.setRepoSlug(slug);
//        mRepoDetailsPresenter.loadBranches();
//        verify(mRepoDetailsView).updateBranches(branches);
//    }
//
//    @Test
//    public void testLoadBranchesFailed() {
//        final String slug = "test";
//
//        final String errorMsg = "error";
//        Exception exception = new Exception(errorMsg);
//        when(mTravisRestClient.getApiService().getBranches(slug)).thenReturn(Single.error(exception));
//
//        mRepoDetailsPresenter.setRepoSlug(slug);
//        mRepoDetailsPresenter.loadBranches();
//        verify(mRepoDetailsView).showBranchesLoadingError(errorMsg);
//    }
//
//    @Test
//    public void testLoadRequests() {
//        final String slug = "test";
//        final List<Build> builds = new ArrayList<>();
//        final List<Commit> commits = new ArrayList<>();
//        final List<RequestData> requestData = new ArrayList<>();
//        final Requests requests = new Requests();
//        requests.setCommits(commits);
//        requests.setRequests(requestData);
//        final BuildHistory buildHistory = new BuildHistory();
//        buildHistory.setBuilds(builds);
//        buildHistory.setCommits(commits);
//
//        when(mTravisRestClient.getApiService().getRequests(slug)).thenReturn(Single.just(requests));
//        when(mTravisRestClient.getApiService().getPullRequestBuilds(slug)).thenReturn(Single.just(buildHistory));
//
//        mRepoDetailsPresenter.setRepoSlug(slug);
//        mRepoDetailsPresenter.loadRequests();
//        verify(mRepoDetailsView).updatePullRequests(requests);
//
//    }
//
//    @Test
//    public void testLoadRequestsFailed() {
//        final String slug = "test";
//
//        final String errorMsg = "error";
//        Exception exception = new Exception(errorMsg);
//        when(mTravisRestClient.getApiService().getRequests(slug)).thenReturn(Single.error(exception));
//        when(mTravisRestClient.getApiService().getPullRequestBuilds(slug)).thenReturn(Single.error(exception));
//
//        mRepoDetailsPresenter.setRepoSlug(slug);
//        mRepoDetailsPresenter.loadRequests();
//        verify(mRepoDetailsView).showPullRequestsLoadingError(errorMsg);
//    }
//
//    @Test
//    public void testSetRepoSlug() {
//        final String slug = "test";
//        mRepoDetailsPresenter.setRepoSlug(slug);
//        assertEquals(slug, mRepoDetailsPresenter.getRepoSlug());
//    }
//
//    @Test
//    public void testLoadData() {
//        final String slug = "test";
//        final List<Build> builds = new ArrayList<>();
//        final List<Commit> commits = new ArrayList<>();
//        final List<RequestData> requestData = new ArrayList<>();
//        final Requests requests = new Requests();
//        requests.setCommits(commits);
//        requests.setRequests(requestData);
//        final BuildHistory buildHistory = new BuildHistory();
//        buildHistory.setBuilds(builds);
//        buildHistory.setCommits(commits);
//        final List<Branch> branch = new ArrayList<>();
//        final Branches branches = new Branches();
//        branches.setBranches(branch);
//        branches.setCommits(commits);
//
//        when(mTravisRestClient.getApiService().getBuilds(slug)).thenReturn(Single.just(buildHistory));
//        when(mTravisRestClient.getApiService().getBranches(slug)).thenReturn(Single.just(branches));
//        when(mTravisRestClient.getApiService().getRequests(slug)).thenReturn(Single.just(requests));
//        when(mTravisRestClient.getApiService().getPullRequestBuilds(slug)).thenReturn(Single.just(buildHistory));
//
//        mRepoDetailsPresenter.setRepoSlug(slug);
//        mRepoDetailsPresenter.loadData();
//        verify(mRepoDetailsPresenter).loadBuildsHistory();
//        verify(mRepoDetailsPresenter).loadBranches();
//        verify(mRepoDetailsPresenter).loadRequests();
//    }
}
