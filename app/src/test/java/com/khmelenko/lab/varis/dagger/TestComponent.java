package com.khmelenko.lab.varis.dagger;

import com.khmelenko.lab.varis.dagger.module.ApplicationModule;
import com.khmelenko.lab.varis.presenter.TestAuthPresenter;
import com.khmelenko.lab.varis.presenter.TestBuildDetailsPresenter;
import com.khmelenko.lab.varis.presenter.TestRepoDetailsPresenter;
import com.khmelenko.lab.varis.presenter.TestRepositoriesPresenter;
import com.khmelenko.lab.varis.presenter.TestSearchResultsPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Component for providing test modules
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Singleton
@Component(modules = {TestNetworkModule.class, TestStorageModule.class, ApplicationModule.class})
public interface TestComponent {

    void inject(TestSearchResultsPresenter test);

    void inject(TestRepositoriesPresenter test);

    void inject(TestRepoDetailsPresenter test);

    void inject(TestBuildDetailsPresenter test);

    void inject(TestAuthPresenter test);

    void inject(TestTaskManager test);

}
