package com.khmelenko.lab.travisclient;

import com.khmelenko.lab.travisclient.event.travis.RestartBuildSuccessEvent;
import com.khmelenko.lab.travisclient.network.retrofit.EmptyOutput;
import com.khmelenko.lab.travisclient.network.retrofit.RestClient;
import com.khmelenko.lab.travisclient.task.TaskHelper;
import com.khmelenko.lab.travisclient.task.TaskManager;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.greenrobot.event.EventBus;

import static org.mockito.Mockito.*;

/**
 * Testing TaskManager class
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class TestTaskManager {

    private TaskManager mTaskManager;
    private RestClient mRestClient;
    private EventBus mEventBus;

    @BeforeClass
    public void setupMock() {
        mRestClient = mock(RestClient.class);
        mEventBus = mock(EventBus.class);

        TaskHelper taskHelper = new TaskHelper(mRestClient, mEventBus);
        mTaskManager = new TaskManager(taskHelper);
    }

    @Test
    public void testRestartBuildTask() {

        when(mRestClient.getApiService().restartBuild(anyLong(), EmptyOutput.INSTANCE)).thenReturn(any());
        doNothing().when(mEventBus).post(new RestartBuildSuccessEvent());

        mTaskManager.restartBuild(0);

        verify(mRestClient).getApiService().restartBuild(anyLong(), EmptyOutput.INSTANCE);
        verify(mEventBus).post(new RestartBuildSuccessEvent());

    }

}
