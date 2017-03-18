package com.khmelenko.lab.varis.dagger.module;

import com.khmelenko.lab.varis.mvp.MvpPresenter;
import com.khmelenko.lab.varis.util.PresenterKeeper;

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
    public PresenterKeeper<MvpPresenter> providePresenterKeeper() {
        return new PresenterKeeper<>();
    }
}
