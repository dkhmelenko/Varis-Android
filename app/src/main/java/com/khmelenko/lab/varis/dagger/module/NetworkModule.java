package com.khmelenko.lab.varis.dagger.module;

import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient;
import com.khmelenko.lab.varis.network.retrofit.raw.RawClient;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Network module
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
public class NetworkModule {

    @Provides
    @Singleton
    public TravisRestClient provideTravisRestClient() {
        return TravisRestClient.newInstance();
    }

    @Provides
    @Singleton
    public GitHubRestClient provideGitHubRestClient() {
        return GitHubRestClient.newInstance();
    }

    @Provides
    @Singleton
    public RawClient provideRawRestClient() {
        return RawClient.newInstance();
    }
}
