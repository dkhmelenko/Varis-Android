package com.khmelenko.lab.travisclient.task;

import com.khmelenko.lab.travisclient.network.retrofit.RestClient;

import de.greenrobot.event.EventBus;

/**
 * Defines an interface for Task
 *
 * @author Dmytro Khmelenko
 */
public abstract class Task<T> {

    private TaskHelper mTaskHelper;

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
     * Sets task helper
     *
     * @param helper Task helper
     */
    public void setHelper(TaskHelper helper) {
        mTaskHelper = helper;
    }

    /**
     * Gets rest client instance
     *
     * @return REST client
     */
    protected RestClient restClient() {
        return mTaskHelper.getRestClient();
    }

    /**
     * Gets event bus instance
     *
     * @return Event bus
     */
    protected EventBus eventBus() {
        return mTaskHelper.getEventBus();
    }

}
