package com.khmelenko.lab.varis.presenter;

import com.khmelenko.lab.varis.BuildConfig;
import com.khmelenko.lab.varis.dagger.DaggerTestComponent;
import com.khmelenko.lab.varis.dagger.TestComponent;
import com.khmelenko.lab.varis.network.response.Repo;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.task.TaskError;
import com.khmelenko.lab.varis.task.TaskException;
import com.khmelenko.lab.varis.task.TaskManager;
import com.khmelenko.lab.varis.view.SearchResultsView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

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
public class TestSearchResultsPresenter {

    @Inject
    TaskManager mTaskManager;

    @Inject
    EventBus mEventBus;

    @Inject
    TravisRestClient mTravisRestClient;

    private SearchResultsPresenter mSearchResultsPresenter;

    private SearchResultsView mSearchResultsView;

    @Before
    public void setup() {
        TestComponent component = DaggerTestComponent.builder().build();
        component.inject(this);

        mSearchResultsPresenter = spy(new SearchResultsPresenter(mTaskManager, mEventBus));
        mSearchResultsView = mock(SearchResultsView.class);
        mSearchResultsPresenter.attach(mSearchResultsView);
    }

    @Test
    public void testStartRepoSearch() {
        final String searchQuery = "test";
        final List<Repo> responseData = new ArrayList<>();
        when(mTravisRestClient.getApiService().getRepos(searchQuery)).thenReturn(responseData);

        mSearchResultsPresenter.startRepoSearch(searchQuery);
        verify(mTaskManager).findRepos(searchQuery);
        verify(mSearchResultsView).hideProgress();
        verify(mSearchResultsView).setSearchResults(eq(responseData));
    }

    @Test
    public void testStartRepoSearchFailed() {
        final String searchQuery = "test";

        final int errorCode = 401;
        final String errorMsg = "error";
        TaskError error = spy(new TaskError(errorCode, errorMsg));
        TaskException exception = spy(new TaskException(error));
        when(mTravisRestClient.getApiService().getRepos(searchQuery)).thenThrow(exception);

        mSearchResultsPresenter.startRepoSearch(searchQuery);
        verify(mTaskManager).findRepos(searchQuery);
        verify(mSearchResultsView).hideProgress();
        verify(mSearchResultsView).showLoadingError(eq(error.getMessage()));
    }
}
