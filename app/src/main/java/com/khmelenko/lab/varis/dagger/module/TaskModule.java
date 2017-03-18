package com.khmelenko.lab.varis.dagger.module;

import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient;
import com.khmelenko.lab.varis.network.retrofit.raw.RawClient;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.task.TaskHelper;
import com.khmelenko.lab.varis.task.TaskManager;

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
    public TaskHelper provideTaskHelper(TravisRestClient travisRestClient,
                                        GitHubRestClient gitHubRestClient,
                                        RawClient rawClient, EventBus eventBus) {
        return new TaskHelper(travisRestClient, gitHubRestClient, rawClient, eventBus);
    }
}

