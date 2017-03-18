package com.khmelenko.lab.varis.task.travis;

import com.khmelenko.lab.varis.event.travis.RestartBuildFailedEvent;
import com.khmelenko.lab.varis.event.travis.RestartBuildSuccessEvent;
import com.khmelenko.lab.varis.network.retrofit.EmptyOutput;
import com.khmelenko.lab.varis.task.Task;
import com.khmelenko.lab.varis.task.TaskError;
import com.khmelenko.lab.varis.task.TaskException;

/**
 * Restart build task
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class RestartBuildTask extends Task<Void> {

    private final long mBuildId;

    public RestartBuildTask(long buildId) {
        mBuildId = buildId;
    }

    @Override
    public Void execute() throws TaskException {
        travisClient().getApiService().restartBuild(mBuildId, EmptyOutput.INSTANCE);
        return null;
    }

    @Override
    public void onSuccess(Void result) {
        RestartBuildSuccessEvent event = new RestartBuildSuccessEvent();
        eventBus().post(event);
    }

    @Override
    public void onFail(TaskError error) {
        RestartBuildFailedEvent event = new RestartBuildFailedEvent(error);
        eventBus().post(event);
    }
}
