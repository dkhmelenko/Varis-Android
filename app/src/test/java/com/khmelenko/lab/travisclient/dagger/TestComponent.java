package com.khmelenko.lab.travisclient.dagger;

import com.khmelenko.lab.travisclient.TestTaskManager;
import com.khmelenko.lab.travisclient.dagger.module.ApplicationModule;
import com.khmelenko.lab.travisclient.dagger.module.StorageModule;
import com.khmelenko.lab.travisclient.presenter.TestSearchResultsPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Component for providing test modules
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Singleton
@Component(modules = {TestNetworkModule.class, TestTaskModule.class, TestNotificationModule.class,
        StorageModule.class, ApplicationModule.class})
public interface TestComponent {

    void inject(TestSearchResultsPresenter test);
    void inject(TestTaskManager test);

}
