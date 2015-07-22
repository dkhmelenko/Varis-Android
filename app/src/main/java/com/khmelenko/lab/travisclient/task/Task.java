package com.khmelenko.lab.travisclient.task;

/**
 * Defines an interface for Task
 *
 * @author Dmytro Khmelenko
 */
public interface Task<T> {

    /**
     * Starts executing task
     *
     * @return Execution result
     */
    T execute() throws TaskException;

    /**
     * Will be called in case of task success
     *
     * @param result Task result
     */
    void onSuccess(T result);

    /**
     * Will be called in case of task error
     *
     * @param error Task error
     */
    void onFail(TaskError error);
}
