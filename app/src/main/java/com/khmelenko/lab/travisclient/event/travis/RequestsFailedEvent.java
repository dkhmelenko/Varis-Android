package com.khmelenko.lab.travisclient.event.travis;

import com.khmelenko.lab.travisclient.task.TaskError;

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
