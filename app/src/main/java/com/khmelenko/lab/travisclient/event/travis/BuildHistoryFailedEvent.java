package com.khmelenko.lab.travisclient.event.travis;

import com.khmelenko.lab.travisclient.task.TaskError;

/**
 * Event on failed loading build history
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class BuildHistoryFailedEvent extends LoadingFailedEvent {

    public BuildHistoryFailedEvent(TaskError taskError) {
        super(taskError);
    }
}
