package com.khmelenko.lab.travisclient.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

/**
 * Notification module
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
public class NotificationModule {

    @Provides
    @Singleton
    public EventBus provideEventBus() {
        return EventBus.getDefault();
    }
}
