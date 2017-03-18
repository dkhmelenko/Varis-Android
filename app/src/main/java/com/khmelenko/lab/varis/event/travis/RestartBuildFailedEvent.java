package com.khmelenko.lab.varis.event.travis;

import com.khmelenko.lab.varis.task.TaskError;

/**
 * Event on failed request Restart build
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public final class RestartBuildFailedEvent extends LoadingFailedEvent {


    public RestartBuildFailedEvent(TaskError taskError) {
        super(taskError);
    }
}
