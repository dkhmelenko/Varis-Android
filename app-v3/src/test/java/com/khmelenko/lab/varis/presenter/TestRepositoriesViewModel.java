package com.khmelenko.lab.varis.presenter;

import com.khmelenko.lab.varis.RxJavaRules;
import com.khmelenko.lab.varis.common.Constants;
import com.khmelenko.lab.varis.dagger.DaggerTestComponent;
import com.khmelenko.lab.varis.dagger.TestComponent;
import com.khmelenko.lab.varis.network.response.Repo;
import com.khmelenko.lab.varis.network.response.User;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.repositories.*;
import com.khmelenko.lab.varis.storage.AppSettings;
import com.khmelenko.lab.varis.storage.CacheStorage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.*;
import org.mockito.ArgumentCaptor;

import android.arch.core.executor.testing.*;
import android.arch.lifecycle.*;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link com.khmelenko.lab.varis.repositories.RepositoriesViewModel}
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class TestRepositoriesViewModel {

    @Rule
    public RxJavaRules mRxJavaRules = new RxJavaRules();

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Inject
    TravisRestClient mTravisRestClient;

    @Inject
    CacheStorage mCacheStorage;

    @Inject
    AppSettings mAppSettings;

    private RepositoriesViewModel mRepositoriesViewModel;

    private Observer<RepositoriesState> stateObserver = mock(Observer.class);

    @Before
    public void setup() {
        TestComponent component = DaggerTestComponent.builder().build();
        component.inject(this);

        final List<Repo> responseData = new ArrayList<>();
        when(mTravisRestClient.getApiService().getRepos("")).thenReturn(Single.just(responseData));

        mRepositoriesViewModel = new RepositoriesViewModel(mTravisRestClient, mCacheStorage, mAppSettings);
        mRepositoriesViewModel.state().observeForever(stateObserver);
    }

    @Test
    public void testReloadRepos() {
        final List<Repo> responseData = new ArrayList<>();
        when(mTravisRestClient.getApiService().getRepos()).thenReturn(Single.just(responseData));

        mRepositoriesViewModel.reloadRepos();

        verify(stateObserver).onChanged(RepositoriesState.Loading.INSTANCE);
        verify(stateObserver).onChanged(new RepositoriesState.ReposList(responseData));
        verifyNoMoreInteractions(stateObserver);
    }

    @Test
    public void testReloadReposWithToken() {
        User user = new User();
        user.setLogin("login");
        when(mTravisRestClient.getApiService().getUser()).thenReturn(Single.just(user));
        when(mAppSettings.getAccessToken()).thenReturn("token");

        mRepositoriesViewModel.reloadRepos();

        verify(stateObserver).onChanged(new RepositoriesState.UserData(user));
        verify(mCacheStorage).saveUser(eq(user));
    }

    @Test
    public void testUserLogout() {
        when(mAppSettings.getServerUrl()).thenReturn(Constants.OPEN_SOURCE_TRAVIS_URL);

        mRepositoriesViewModel.userLogout();
        verify(mCacheStorage).deleteUser();
        verify(mCacheStorage).deleteRepos();
        verify(mTravisRestClient).updateTravisEndpoint(eq(Constants.OPEN_SOURCE_TRAVIS_URL));
    }

}
