package com.khmelenko.lab.travisclient.component;

import com.khmelenko.lab.travisclient.activity.MainActivity;
import com.khmelenko.lab.travisclient.fragment.AuthFragment;
import com.khmelenko.lab.travisclient.module.NetworkModule;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskHelper;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Network component
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Singleton
@Component(modules = {NetworkModule.class})
public interface NetworkComponent {

    void inject(MainActivity activity);

    void inject(AuthFragment fragment);

    void inject(TaskHelper task);
}
