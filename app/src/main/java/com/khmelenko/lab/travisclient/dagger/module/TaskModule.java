package com.khmelenko.lab.travisclient.dagger.module;

import com.khmelenko.lab.travisclient.task.TaskManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Task module
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
public class TaskModule {

    @Provides
    @Singleton
    public TaskManager provideTaskManager() {
        return new TaskManager();
    }
}

