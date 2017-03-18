package com.khmelenko.lab.varis.event.travis;

import com.khmelenko.lab.varis.task.TaskError;

/**
 * Event on failed loading data
 *
 * @author Dmytro Khmelenko
 */
public class LoadingFailedEvent {

    private final TaskError mTaskError;

    public LoadingFailedEvent(TaskError taskError) {
        mTaskError = taskError;
    }

    public TaskError getTaskError() {
        return mTaskError;
    }
}
