package com.khmelenko.lab.varis.dagger.component;

import com.khmelenko.lab.varis.dagger.module.ApplicationModule;
import com.khmelenko.lab.varis.dagger.module.NetworkModule;
import com.khmelenko.lab.varis.dagger.module.StorageModule;
import com.khmelenko.lab.varis.dagger.module.TaskModule;
import com.khmelenko.lab.varis.mvp.MvpPresenter;
import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClientRx;
import com.khmelenko.lab.varis.network.retrofit.raw.RawClientRx;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClientRx;
import com.khmelenko.lab.varis.storage.CacheStorage;
import com.khmelenko.lab.varis.util.PresenterKeeper;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Base component
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Singleton
@Component(modules = {NetworkModule.class, TaskModule.class, StorageModule.class, ApplicationModule.class})
public interface BaseComponent {

    TravisRestClientRx restClient();

    RawClientRx rawClient();

    GitHubRestClientRx gitHubClient();

    CacheStorage cache();

    PresenterKeeper<MvpPresenter> presenterKeeper();
}
