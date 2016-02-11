package com.khmelenko.lab.travisclient.task.travis;

import com.khmelenko.lab.travisclient.event.travis.CancelBuildSuccessEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.RestartBuildSuccessEvent;
import com.khmelenko.lab.travisclient.network.retrofit.EmptyOutput;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

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
        restClient().getApiService().cancelBuild(mBuildId, EmptyOutput.INSTANCE);
        return null;
    }

    @Override
    public void onSuccess(Void result) {
        CancelBuildSuccessEvent event = new CancelBuildSuccessEvent();
        eventBus().post(event);
    }

    @Override
    public void onFail(TaskError error) {
        LoadingFailedEvent event = new LoadingFailedEvent(error);
        eventBus().post(event);
    }
}
