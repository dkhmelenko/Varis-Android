package com.khmelenko.lab.travisclient.presenter;

import com.khmelenko.lab.travisclient.BuildConfig;
import com.khmelenko.lab.travisclient.common.Constants;
import com.khmelenko.lab.travisclient.dagger.DaggerTestComponent;
import com.khmelenko.lab.travisclient.dagger.TestComponent;
import com.khmelenko.lab.travisclient.network.response.Repo;
import com.khmelenko.lab.travisclient.network.response.User;
import com.khmelenko.lab.travisclient.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.travisclient.storage.AppSettings;
import com.khmelenko.lab.travisclient.storage.CacheStorage;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.view.RepositoriesView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

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
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TestRepositoriesPresenter {

    @Inject
    TaskManager mTaskManager;

    @Inject
    EventBus mEventBus;

    @Inject
    TravisRestClient mTravisRestClient;

    @Inject
    CacheStorage mCacheStorage;

    private RepositoriesPresenter mRepositoriesPresenter;

    private RepositoriesView mRepositoriesView;

    @Before
    public void setup() {
        TestComponent component = DaggerTestComponent.builder().build();
        component.inject(this);

        mRepositoriesPresenter = spy(new RepositoriesPresenter(mTravisRestClient, mEventBus, mTaskManager, mCacheStorage));
        mRepositoriesView = mock(RepositoriesView.class);
        mRepositoriesPresenter.attach(mRepositoriesView);
    }

    @Test
    public void testReloadRepos() {
        final List<Repo> responseData = new ArrayList<>();
        when(mTravisRestClient.getApiService().getRepos()).thenReturn(responseData);

        mRepositoriesPresenter.reloadRepos();
        verify(mTaskManager, times(2)).findRepos(null);
        verify(mRepositoriesView, times(2)).hideProgress();
        verify(mRepositoriesView, times(2)).setRepos(eq(responseData));
    }

    @Test
    public void testReloadReposWithToken() {
        User user = new User();
        user.setLogin("login");
        when(mTravisRestClient.getApiService().getUser()).thenReturn(user);

        // pre-setting access token
        AppSettings.putAccessToken("token");

        mRepositoriesPresenter.reloadRepos();
        verify(mTaskManager).getUser();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mRepositoriesView, times(2)).updateUserData(userCaptor.capture());
        assertNotNull(userCaptor.getValue());
        assertEquals(user.getLogin(), userCaptor.getValue().getLogin());

        verify(mCacheStorage).saveUser(eq(user));
        verify(mTaskManager).userRepos(eq(user.getLogin()));
    }

    @Test
    public void testUserLogout() {
        mRepositoriesPresenter.userLogout();
        verify(mCacheStorage).deleteUser();
        verify(mCacheStorage).deleteRepos();
        verify(mTravisRestClient).updateTravisEndpoint(eq(Constants.OPEN_SOURCE_TRAVIS_URL));
    }

}
