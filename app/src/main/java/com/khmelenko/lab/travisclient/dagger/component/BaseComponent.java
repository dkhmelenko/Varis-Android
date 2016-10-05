package com.khmelenko.lab.travisclient.dagger.component;

import com.khmelenko.lab.travisclient.dagger.module.ApplicationModule;
import com.khmelenko.lab.travisclient.dagger.module.NetworkModule;
import com.khmelenko.lab.travisclient.dagger.module.NotificationModule;
import com.khmelenko.lab.travisclient.dagger.module.StorageModule;
import com.khmelenko.lab.travisclient.dagger.module.TaskModule;
import com.khmelenko.lab.travisclient.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.travisclient.storage.CacheStorage;
import com.khmelenko.lab.travisclient.task.TaskManager;
import com.khmelenko.lab.travisclient.util.PresenterKeeper;

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

    EventBus eventBus();

    TaskManager taskManager();

    CacheStorage cache();

    PresenterKeeper presenterKeeper();
}
