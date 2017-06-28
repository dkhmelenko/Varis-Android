package com.khmelenko.lab.varis.presenter;

import com.khmelenko.lab.varis.RxJavaRules;
import com.khmelenko.lab.varis.dagger.DaggerTestComponent;
import com.khmelenko.lab.varis.dagger.TestComponent;
import com.khmelenko.lab.varis.network.response.Repo;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.view.SearchResultsView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

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
public class TestSearchResultsPresenter {

    @Rule
    public RxJavaRules mRxJavaRules = new RxJavaRules();

    @Inject
    TravisRestClient mTravisRestClient;

    private SearchResultsPresenter mSearchResultsPresenter;

    private SearchResultsView mSearchResultsView;

    @Before
    public void setup() {
        TestComponent component = DaggerTestComponent.builder().build();
        component.inject(this);

        mSearchResultsPresenter = spy(new SearchResultsPresenter(mTravisRestClient));
        mSearchResultsView = mock(SearchResultsView.class);
        mSearchResultsPresenter.attach(mSearchResultsView);
    }

    @Test
    public void testStartRepoSearch() {
        final String searchQuery = "test";
        final List<Repo> responseData = new ArrayList<>();
        when(mTravisRestClient.getApiService().getRepos(searchQuery)).thenReturn(Single.just(responseData));

        mSearchResultsPresenter.startRepoSearch(searchQuery);
        verify(mSearchResultsView).hideProgress();
        verify(mSearchResultsView).setSearchResults(eq(responseData));
    }

    @Test
    public void testStartRepoSearchFailed() {
        final String searchQuery = "test";

        final String errorMsg = "error";
        Exception exception = new Exception(errorMsg);
        when(mTravisRestClient.getApiService().getRepos(searchQuery)).thenReturn(Single.error(exception));

        mSearchResultsPresenter.startRepoSearch(searchQuery);
        verify(mSearchResultsView).hideProgress();
        verify(mSearchResultsView).showLoadingError(eq(exception.getMessage()));
    }
}
