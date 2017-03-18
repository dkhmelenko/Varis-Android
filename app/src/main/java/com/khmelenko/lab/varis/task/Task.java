package com.khmelenko.lab.varis.task;

import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient;
import com.khmelenko.lab.varis.network.retrofit.raw.RawClient;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;

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
    protected TravisRestClient travisClient() {
        return mTaskHelper.getTravisRestClient();
    }

    /**
     * Gets rest client instance
     *
     * @return REST client
     */
    protected GitHubRestClient gitHubClient() {
        return mTaskHelper.getGitHubRestClient();
    }

    /**
     * Gets rest client instance
     *
     * @return REST client
     */
    protected RawClient rawClient() {
        return mTaskHelper.getRawClient();
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
