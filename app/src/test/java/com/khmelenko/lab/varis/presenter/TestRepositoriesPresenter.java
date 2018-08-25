package com.khmelenko.lab.varis.viewmodel;

import com.khmelenko.lab.varis.RxJavaRules;
import com.khmelenko.lab.varis.common.Constants;
import com.khmelenko.lab.varis.dagger.DaggerTestComponent;
import com.khmelenko.lab.varis.dagger.TestComponent;
import com.khmelenko.lab.varis.network.response.Repo;
import com.khmelenko.lab.varis.network.response.User;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.repositories.RepositoriesPresenter;
import com.khmelenko.lab.varis.storage.AppSettings;
import com.khmelenko.lab.varis.storage.CacheStorage;
import com.khmelenko.lab.varis.repositories.RepositoriesView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link RepositoriesPresenter}
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class TestRepositoriesPresenter {

    @Rule
    public RxJavaRules mRxJavaRules = new RxJavaRules();

    @Inject
    TravisRestClient mTravisRestClient;

    @Inject
    CacheStorage mCacheStorage;

    @Inject
    AppSettings mAppSettings;

    private RepositoriesPresenter mRepositoriesPresenter;

    private RepositoriesView mRepositoriesView;

    @Before
    public void setup() {
        TestComponent component = DaggerTestComponent.builder().build();
        component.inject(this);

        final List<Repo> responseData = new ArrayList<>();
        when(mTravisRestClient.getApiService().getRepos("")).thenReturn(Single.just(responseData));

        mRepositoriesPresenter = spy(new RepositoriesPresenter(mTravisRestClient, mCacheStorage, mAppSettings));
        mRepositoriesView = mock(RepositoriesView.class);
        mRepositoriesPresenter.attach(mRepositoriesView);
    }

    @Test
    public void testReloadRepos() {
        final List<Repo> responseData = new ArrayList<>();
        when(mTravisRestClient.getApiService().getRepos()).thenReturn(Single.just(responseData));

        mRepositoriesPresenter.reloadRepos();
        verify(mRepositoriesView, times(2)).hideProgress();
        verify(mRepositoriesView, times(2)).setRepos(eq(responseData));
    }

    @Test
    public void testReloadReposWithToken() {
        User user = new User();
        user.setLogin("login");
        when(mTravisRestClient.getApiService().getUser()).thenReturn(Single.just(user));
        when(mAppSettings.getAccessToken()).thenReturn("token");

        mRepositoriesPresenter.reloadRepos();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mRepositoriesView, times(2)).updateUserData(userCaptor.capture());
        assertNotNull(userCaptor.getValue());
        assertEquals(user.getLogin(), userCaptor.getValue().getLogin());

        verify(mCacheStorage).saveUser(eq(user));
    }

    @Test
    public void testUserLogout() {
        when(mAppSettings.getServerUrl()).thenReturn(Constants.OPEN_SOURCE_TRAVIS_URL);

        mRepositoriesPresenter.userLogout();
        verify(mCacheStorage).deleteUser();
        verify(mCacheStorage).deleteRepos();
        verify(mTravisRestClient).updateTravisEndpoint(eq(Constants.OPEN_SOURCE_TRAVIS_URL));
    }

}
