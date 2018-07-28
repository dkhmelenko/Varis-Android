package com.khmelenko.lab.varis.dagger.component;

import com.khmelenko.lab.varis.TravisApp;
import com.khmelenko.lab.varis.auth.AuthActivityModule;
import com.khmelenko.lab.varis.builddetails.BuildDetailsActivityModule;
import com.khmelenko.lab.varis.dagger.module.ApplicationModule;
import com.khmelenko.lab.varis.dagger.module.NetworkModule;
import com.khmelenko.lab.varis.dagger.module.StorageModule;
import com.khmelenko.lab.varis.log.LogsParser;
import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient;
import com.khmelenko.lab.varis.network.retrofit.raw.RawClient;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.repodetails.RepoDetailsActivityModule;
import com.khmelenko.lab.varis.repositories.MainActivityModule;
import com.khmelenko.lab.varis.repositories.search.SearchResultsActivityModule;
import com.khmelenko.lab.varis.storage.AppSettings;
import com.khmelenko.lab.varis.storage.CacheStorage;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;
import javax.inject.Singleton;

/**
 * Base component
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Singleton
@Component(modules = {NetworkModule.class, StorageModule.class, ApplicationModule.class,
        AuthActivityModule.class, BuildDetailsActivityModule.class, MainActivityModule.class,
        RepoDetailsActivityModule.class, SearchResultsActivityModule.class, AndroidSupportInjectionModule.class})
public interface BaseComponent {

    void inject(TravisApp app);

    TravisRestClient restClient();

    RawClient rawClient();

    GitHubRestClient gitHubClient();

    CacheStorage cache();

    AppSettings appSettings();

    LogsParser logsParser();
}
