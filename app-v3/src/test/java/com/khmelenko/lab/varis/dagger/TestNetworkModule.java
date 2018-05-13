package com.khmelenko.lab.varis.dagger;

import com.khmelenko.lab.varis.log.LogsParser;
import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient;
import com.khmelenko.lab.varis.network.retrofit.github.GithubApiService;
import com.khmelenko.lab.varis.network.retrofit.raw.RawApiService;
import com.khmelenko.lab.varis.network.retrofit.raw.RawClient;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisApiService;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

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
        TravisRestClient travisRestClient = mock(TravisRestClient.class);
        TravisApiService apiService = mock(TravisApiService.class);
        Mockito.when(travisRestClient.getApiService()).thenReturn(apiService);
        return travisRestClient;
    }

    @Singleton
    @Provides
    public GitHubRestClient provideGitHubRestClient() {
        GitHubRestClient gitHubRestClient = mock(GitHubRestClient.class);
        GithubApiService githubApiService = mock(GithubApiService.class);
        Mockito.when(gitHubRestClient.getApiService()).thenReturn(githubApiService);
        return gitHubRestClient;
    }

    @Singleton
    @Provides
    public RawClient provideRawRestClient() {
        RawClient rawClient = mock(RawClient.class);
        RawApiService rawApiService = mock(RawApiService.class);
        Mockito.when(rawClient.getApiService()).thenReturn(rawApiService);
        return rawClient;
    }

    @Singleton
    @Provides
    public LogsParser provideLogsParser() {
        return mock(LogsParser.class);
    }
}
