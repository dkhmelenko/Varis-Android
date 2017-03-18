package com.khmelenko.lab.varis.dagger;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

/**
 * Notification module for testing
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
public class TestNotificationModule {

    @Singleton
    @Provides
    public EventBus provideEventBus() {
        return Mockito.spy(EventBus.class);
    }

}
