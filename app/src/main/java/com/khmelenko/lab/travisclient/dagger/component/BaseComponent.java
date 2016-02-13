package com.khmelenko.lab.travisclient.dagger.component;

import com.khmelenko.lab.travisclient.dagger.module.NetworkModule;
import com.khmelenko.lab.travisclient.dagger.module.NotificationModule;
import com.khmelenko.lab.travisclient.dagger.module.StorageModule;
import com.khmelenko.lab.travisclient.dagger.module.TaskModule;
import com.khmelenko.lab.travisclient.network.retrofit.RestClient;
import com.khmelenko.lab.travisclient.storage.CacheStorage;
import com.khmelenko.lab.travisclient.task.TaskManager;

import javax.inject.Singleton;

import dagger.Component;
import de.greenrobot.event.EventBus;

/**
 * Base component
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Singleton
@Component(modules = {NetworkModule.class, NotificationModule.class, TaskModule.class, StorageModule.class})
public interface BaseComponent {

    RestClient restClient();

    EventBus eventBus();

    TaskManager taskManager();

    CacheStorage cache();
}
