package com.khmelenko.lab.travisclient.event.travis;

import com.khmelenko.lab.travisclient.task.TaskError;

/**
 * Event on failed loading log file
 *
 * @author Dmytro Khmelenko
 */
public final class LogFailEvent {

    private final TaskError mTaskError;

    public LogFailEvent(TaskError taskError) {
        mTaskError = taskError;
    }

    public TaskError getTaskError() {
        return mTaskError;
    }
}
