package com.khmelenko.lab.travisclient.dagger.module;

import com.khmelenko.lab.travisclient.network.retrofit.RestClient;
import com.khmelenko.lab.travisclient.task.TaskHelper;
import com.khmelenko.lab.travisclient.task.TaskManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

/**
 * Task module
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
public class TaskModule {

    @Provides
    @Singleton
    public TaskManager provideTaskManager(TaskHelper taskHelper) {
        return new TaskManager(taskHelper);
    }

    @Provides
    @Singleton
    public TaskHelper provideTaskHelper(RestClient restClient, EventBus eventBus) {
        return new TaskHelper(restClient, eventBus);
    }
}

