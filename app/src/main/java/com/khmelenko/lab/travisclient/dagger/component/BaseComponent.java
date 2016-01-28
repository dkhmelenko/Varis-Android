package com.khmelenko.lab.travisclient.dagger.component;

import com.khmelenko.lab.travisclient.dagger.module.NetworkModule;
import com.khmelenko.lab.travisclient.dagger.module.NotificationModule;
import com.khmelenko.lab.travisclient.network.retrofit.RestClient;

import javax.inject.Singleton;

import dagger.Component;
import de.greenrobot.event.EventBus;

/**
 * Base component
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Singleton
@Component(modules = {NetworkModule.class, NotificationModule.class})
public interface BaseComponent {

    RestClient restClient();

    EventBus eventBus();
}
