package com.khmelenko.lab.varis.dagger;

import com.khmelenko.lab.varis.dagger.module.ApplicationModule;
import com.khmelenko.lab.varis.viewmodel.TestAuthPresenter;
import com.khmelenko.lab.varis.viewmodel.TestBuildDetailsPresenter;
import com.khmelenko.lab.varis.viewmodel.TestRepoDetailsPresenter;
import com.khmelenko.lab.varis.viewmodel.TestRepositoriesPresenter;
import com.khmelenko.lab.varis.viewmodel.TestSearchResultsPresenter;

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

}
