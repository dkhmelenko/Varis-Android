package com.khmelenko.lab.varis.event.travis;

import com.khmelenko.lab.varis.task.TaskError;

/**
 * Event on failed loading build history
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public final class BuildHistoryFailedEvent extends LoadingFailedEvent {

    public BuildHistoryFailedEvent(TaskError taskError) {
        super(taskError);
    }
}
