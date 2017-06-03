package com.khmelenko.lab.varis.dagger.component;

import com.khmelenko.lab.varis.dagger.module.ApplicationModule;
import com.khmelenko.lab.varis.dagger.module.NetworkModule;
import com.khmelenko.lab.varis.dagger.module.NotificationModule;
import com.khmelenko.lab.varis.dagger.module.StorageModule;
import com.khmelenko.lab.varis.dagger.module.TaskModule;
import com.khmelenko.lab.varis.mvp.MvpPresenter;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClientRx;
import com.khmelenko.lab.varis.storage.CacheStorage;
import com.khmelenko.lab.varis.task.TaskManager;
import com.khmelenko.lab.varis.util.PresenterKeeper;

import javax.inject.Singleton;

import dagger.Component;
import de.greenrobot.event.EventBus;

/**
 * Base component
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Singleton
@Component(modules = {NetworkModule.class, NotificationModule.class, TaskModule.class,
        StorageModule.class, ApplicationModule.class})
public interface BaseComponent {

    TravisRestClient restClient();

    TravisRestClientRx restClientRx();

    EventBus eventBus();

    TaskManager taskManager();

    CacheStorage cache();

    PresenterKeeper<MvpPresenter> presenterKeeper();
}
