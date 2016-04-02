package com.khmelenko.lab.travisclient.event.travis;

import com.khmelenko.lab.travisclient.task.TaskError;

/**
 * Event on failed loading branches
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class BranchesFailedEvent extends LoadingFailedEvent {

    public BranchesFailedEvent(TaskError taskError) {
        super(taskError);
    }
}
