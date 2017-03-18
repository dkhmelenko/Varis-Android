package com.khmelenko.lab.varis.event.travis;

import com.khmelenko.lab.varis.task.TaskError;

/**
 * Event on failed loading requests
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public final class RequestsFailedEvent extends LoadingFailedEvent {

    public RequestsFailedEvent(TaskError taskError) {
        super(taskError);
    }
}
