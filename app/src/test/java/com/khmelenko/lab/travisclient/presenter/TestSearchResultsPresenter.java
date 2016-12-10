package com.khmelenko.lab.travisclient.presenter;

import com.khmelenko.lab.travisclient.BuildConfig;
import com.khmelenko.lab.travisclient.network.response.Repo;
import com.khmelenko.lab.travisclient.network.retrofit.github.GitHubRestClient;
import com.khmelenko.lab.travisclient.network.retrofit.github.GithubApiService;
import com.khmelenko.lab.travisclient.network.retrofit.raw.RawApiService;
import com.khmelenko.lab.travisclient.network.retrofit.raw.RawClient;
import com.khmelenko.lab.travisclient.network.retrofit.travis.TravisApiService;
import com.khmelenko.lab.travisclient.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.travisclient.task.TaskHelper;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.view.SearchResultsView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link SearchResultsPresenter}
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({TravisRestClient.class, GitHubRestClient.class, RawClient.class,
        TaskHelper.class, TaskManager.class, SearchResultsPresenter.class})
public class TestSearchResultsPresenter {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private TaskManager mTaskManager;
    private TaskHelper mTaskHelper;
    private EventBus mEventBus;
    private TravisRestClient mTravisRestClient;
    private GitHubRestClient mGitHubRestClient;
    private RawClient mRawClient;

    private SearchResultsPresenter mSearchResultsPresenter;

    @Mock
    private SearchResultsView mSearchResultsView;

    @Before
    public void setup() {
        mTravisRestClient = mock(TravisRestClient.class);
        TravisApiService apiService = mock(TravisApiService.class);
        when(mTravisRestClient.getApiService()).thenReturn(apiService);

        mGitHubRestClient = mock(GitHubRestClient.class);
        GithubApiService githubApiService = mock(GithubApiService.class);
        when(mGitHubRestClient.getApiService()).thenReturn(githubApiService);

        mRawClient = mock(RawClient.class);
        RawApiService rawApiService = mock(RawApiService.class);
        when(mRawClient.getApiService()).thenReturn(rawApiService);

        mEventBus = spy(EventBus.class);

        mTaskHelper = spy(new TaskHelper(mTravisRestClient, mGitHubRestClient, mRawClient, mEventBus));
        mTaskManager = spy(new TaskManager(mTaskHelper));

        mSearchResultsPresenter = spy(new SearchResultsPresenter(mTaskManager, mEventBus));
        mSearchResultsView = mock(SearchResultsView.class);
        mSearchResultsPresenter.attach(mSearchResultsView);
    }

    @Test
    public void testAttachDetach() {
        mSearchResultsPresenter.attach(mSearchResultsView);
        verify(mSearchResultsPresenter).onAttach();
        verify(mEventBus).register(mSearchResultsPresenter);
        assertNotNull(mSearchResultsPresenter.getView());

        mSearchResultsPresenter.detach();
        verify(mSearchResultsPresenter).onDetach();
        verify(mEventBus).unregister(mSearchResultsPresenter);
        assertNull(mSearchResultsPresenter.getView());
    }

    @Test
    public void testStartRepoSearch() {
        final String searchQuery = "test";
        final List<Repo> responseData = new ArrayList<>();
        mSearchResultsPresenter.startRepoSearch(searchQuery);
        verify(mTaskManager).findRepos(searchQuery);
        verify(mSearchResultsView).hideProgress();
        verify(mSearchResultsView).setSearchResults(eq(responseData));
    }
}
