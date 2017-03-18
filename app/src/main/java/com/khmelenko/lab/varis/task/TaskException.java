package com.khmelenko.lab.varis.task;

/**
 * Exception for task
 * @author Dmytro Khmelenko
 */
public class TaskException extends RuntimeException {

    private final TaskError mTaskError;

    public TaskException(TaskError error) {
        mTaskError = error;
    }

    public TaskError getTaskError() {
        return mTaskError;
    }
}
