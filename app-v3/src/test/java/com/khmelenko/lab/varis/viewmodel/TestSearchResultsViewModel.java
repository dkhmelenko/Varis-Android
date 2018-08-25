package com.khmelenko.lab.varis.viewmodel;

import com.khmelenko.lab.varis.RxJavaRules;
import com.khmelenko.lab.varis.repositories.search.SearchResultsViewModel;

import org.junit.Rule;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Testing {@link SearchResultsViewModel}
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class TestSearchResultsViewModel {

    @Rule
    public RxJavaRules mRxJavaRules = new RxJavaRules();

    // TODO
//    @Inject
//    TravisRestClient mTravisRestClient;
//
//    private SearchResultsViewModel mSearchResultsViewModel;
//
//    private SearchResultsView mSearchResultsView;
//
//    @Before
//    public void setup() {
//        TestComponent component = DaggerTestComponent.builder().build();
//        component.inject(this);
//
//        mSearchResultsViewModel = spy(new SearchResultsViewModel(mTravisRestClient));
//        mSearchResultsView = mock(SearchResultsView.class);
//        mSearchResultsViewModel.attach(mSearchResultsView);
//    }
//
//    @Test
//    public void testStartRepoSearch() {
//        final String searchQuery = "test";
//        final List<Repo> responseData = new ArrayList<>();
//        when(mTravisRestClient.getApiService().getRepos(searchQuery)).thenReturn(Single.just(responseData));
//
//        mSearchResultsViewModel.startRepoSearch(searchQuery);
//        verify(mSearchResultsView).hideProgress();
//        verify(mSearchResultsView).setSearchResults(eq(responseData));
//    }
//
//    @Test
//    public void testStartRepoSearchFailed() {
//        final String searchQuery = "test";
//
//        final String errorMsg = "error";
//        Exception exception = new Exception(errorMsg);
//        when(mTravisRestClient.getApiService().getRepos(searchQuery)).thenReturn(Single.error(exception));
//
//        mSearchResultsViewModel.startRepoSearch(searchQuery);
//        verify(mSearchResultsView).hideProgress();
//        verify(mSearchResultsView).showLoadingError(eq(exception.getMessage()));
//    }
}
