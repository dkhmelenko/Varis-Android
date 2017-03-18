package com.khmelenko.lab.varis.task.travis;

import com.khmelenko.lab.varis.event.travis.CancelBuildFailedEvent;
import com.khmelenko.lab.varis.event.travis.CancelBuildSuccessEvent;
import com.khmelenko.lab.varis.network.retrofit.EmptyOutput;
import com.khmelenko.lab.varis.task.Task;
import com.khmelenko.lab.varis.task.TaskError;
import com.khmelenko.lab.varis.task.TaskException;

/**
 * Cancel build task
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class CancelBuildTask extends Task<Void> {

    private final long mBuildId;

    public CancelBuildTask(long buildId) {
        mBuildId = buildId;
    }

    @Override
    public Void execute() throws TaskException {
        travisClient().getApiService().cancelBuild(mBuildId, EmptyOutput.INSTANCE);
        return null;
    }

    @Override
    public void onSuccess(Void result) {
        CancelBuildSuccessEvent event = new CancelBuildSuccessEvent();
        eventBus().post(event);
    }

    @Override
    public void onFail(TaskError error) {
        CancelBuildFailedEvent event = new CancelBuildFailedEvent(error);
        eventBus().post(event);
    }
}
