package com.khmelenko.lab.travisclient.task;

/**
 * Defines the result of the task execution
 *
 * @author Dmytro Khmelenko
 */
public final class TaskResult<T> {

    private final T mResult;
    private final TaskError mTaskError;

    public TaskResult(T result, TaskError taskError) {
        mResult = result;
        mTaskError = taskError;
    }

    public T getResult() {
        return mResult;
    }

    public TaskError getTaskError() {
        return mTaskError;
    }

    public boolean isSuccess() {
        return mTaskError == null;
    }
}
