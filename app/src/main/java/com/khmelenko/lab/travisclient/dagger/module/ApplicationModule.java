package com.khmelenko.lab.travisclient.dagger.module;

import com.khmelenko.lab.travisclient.util.PresenterKeeper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Application module
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
public class ApplicationModule {

    @Provides
    @Singleton
    public PresenterKeeper providePresenterKeeper() {
        return new PresenterKeeper();
    }
}
