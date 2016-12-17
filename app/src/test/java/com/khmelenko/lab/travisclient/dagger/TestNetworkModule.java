package com.khmelenko.lab.travisclient.dagger;

import com.khmelenko.lab.travisclient.network.retrofit.github.GitHubRestClient;
import com.khmelenko.lab.travisclient.network.retrofit.github.GithubApiService;
import com.khmelenko.lab.travisclient.network.retrofit.raw.RawApiService;
import com.khmelenko.lab.travisclient.network.retrofit.raw.RawClient;
import com.khmelenko.lab.travisclient.network.retrofit.travis.TravisApiService;
import com.khmelenko.lab.travisclient.network.retrofit.travis.TravisRestClient;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * NetworkModule for testing
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
public class TestNetworkModule {

    @Singleton
    @Provides
    public TravisRestClient provideTravisRestClient() {
        TravisRestClient travisRestClient = Mockito.mock(TravisRestClient.class);
        TravisApiService apiService = Mockito.mock(TravisApiService.class);
        Mockito.when(travisRestClient.getApiService()).thenReturn(apiService);
        return travisRestClient;
    }

    @Singleton
    @Provides
    public GitHubRestClient provideGitHubRestClient() {
        GitHubRestClient gitHubRestClient = Mockito.mock(GitHubRestClient.class);
        GithubApiService githubApiService = Mockito.mock(GithubApiService.class);
        Mockito.when(gitHubRestClient.getApiService()).thenReturn(githubApiService);
        return gitHubRestClient;
    }

    @Singleton
    @Provides
    public RawClient provideRawRestClient() {
        RawClient rawClient = Mockito.mock(RawClient.class);
        RawApiService rawApiService = Mockito.mock(RawApiService.class);
        Mockito.when(rawClient.getApiService()).thenReturn(rawApiService);
        return rawClient;
    }
}
