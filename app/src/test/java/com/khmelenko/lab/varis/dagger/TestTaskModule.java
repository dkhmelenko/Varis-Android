package com.khmelenko.lab.varis.dagger;

import com.khmelenko.lab.varis.task.TaskHelper;
import com.khmelenko.lab.varis.task.TaskManager;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

/**
 * TaskModule for testing
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
public class TestTaskModule {

    @Singleton
    @Provides
    public TaskHelper provideTaskHelper(TravisRestClient travisRestClient,
                                        GitHubRestClient gitHubRestClient,
                                        RawClient rawClient, EventBus eventBus) {
        return new TaskHelper(travisRestClient, gitHubRestClient, rawClient, eventBus);
    }

    @Singleton
    @Provides
    public TaskManager provideTaskManager(TaskHelper taskHelper) {
        return Mockito.spy(new TaskManager(taskHelper));
    }

}
