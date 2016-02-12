package com.khmelenko.lab.travisclient.event.travis;

import com.khmelenko.lab.travisclient.task.TaskError;

/**
 * Event on failed request Cancel build
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public final class CancelBuildFailedEvent extends LoadingFailedEvent {


    public CancelBuildFailedEvent(TaskError taskError) {
        super(taskError);
    }
}
