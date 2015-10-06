package com.khmelenko.lab.travisclient.task;

import com.khmelenko.lab.travisclient.network.retrofit.RestClient;

import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;

/**
 * Defines an interface for Task
 *
 * @author Dmytro Khmelenko
 */
public abstract class Task<T> {

    // TODO Move to DI
    protected RestClient mRestClient = RestClient.getInstance();

    protected EventBus mEventBus = EventBus.getDefault();

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

}
