package com.khmelenko.lab.travisclient.task;

import com.khmelenko.lab.travisclient.network.retrofit.RestClient;

import de.greenrobot.event.EventBus;

/**
 * Defines an interface for Task
 *
 * @author Dmytro Khmelenko
 */
public abstract class Task<T> {

    private TaskHelper mTaskHelper = new TaskHelper();

    /**
     * Starts executing task
     *
     * @return Execution result
     */
    public abstract T execute() throws TaskException;

    /**
     * Will be called in case of task success
     *
     * @param result Task result
     */
    public abstract void onSuccess(T result);

    /**
     * Will be called in case of task error
     *
     * @param error Task error
     */
    public abstract void onFail(TaskError error);

    /**
     * Gets rest client instance
     *
     * @return REST client
     */
    protected RestClient restClient() {
        return mTaskHelper.mRestClient;
    }

    protected EventBus eventBus() {
        return mTaskHelper.mEventBus;
    }
}
