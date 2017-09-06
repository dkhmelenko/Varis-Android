package com.khmelenko.lab.varis.dagger.component;

import com.khmelenko.lab.varis.dagger.module.ApplicationModule;
import com.khmelenko.lab.varis.dagger.module.NetworkModule;
import com.khmelenko.lab.varis.dagger.module.StorageModule;
import com.khmelenko.lab.varis.log.LogsParser;
import com.khmelenko.lab.varis.mvp.MvpPresenter;
import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient;
import com.khmelenko.lab.varis.network.retrofit.raw.RawClient;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.storage.AppSettings;
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
@Component(modules = {NetworkModule.class, StorageModule.class, ApplicationModule.class})
public interface BaseComponent {

    TravisRestClient restClient();

    RawClient rawClient();

    GitHubRestClient gitHubClient();

    CacheStorage cache();

    AppSettings appSettings();

    PresenterKeeper<MvpPresenter> presenterKeeper();

    LogsParser logsParser();
}
