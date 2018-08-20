package com.khmelenko.lab.varis.dagger

import com.khmelenko.lab.varis.log.LogsParser
import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient
import com.khmelenko.lab.varis.network.retrofit.github.GithubApiService
import com.khmelenko.lab.varis.network.retrofit.raw.RawApiService
import com.khmelenko.lab.varis.network.retrofit.raw.RawClient
import com.khmelenko.lab.varis.network.retrofit.travis.TravisApiService
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import com.nhaarman.mockito_kotlin.whenever
import dagger.Module
import dagger.Provides
import org.mockito.Mockito.mock
import javax.inject.Singleton

/**
 * NetworkModule for testing
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
class TestNetworkModule {

    @Singleton
    @Provides
    fun provideTravisRestClient(): TravisRestClient {
        val travisRestClient = mock(TravisRestClient::class.java)
        val apiService = mock(TravisApiService::class.java)
        whenever(travisRestClient.apiService).thenReturn(apiService)
        return travisRestClient
    }

    @Singleton
    @Provides
    fun provideGitHubRestClient(): GitHubRestClient {
        val gitHubRestClient = mock(GitHubRestClient::class.java)
        val githubApiService = mock(GithubApiService::class.java)
        whenever(gitHubRestClient.apiService).thenReturn(githubApiService)
        return gitHubRestClient
    }

    @Singleton
    @Provides
    fun provideRawRestClient(): RawClient {
        val rawClient = mock(RawClient::class.java)
        val rawApiService = mock(RawApiService::class.java)
        whenever(rawClient.apiService).thenReturn(rawApiService)
        return rawClient
    }

    @Singleton
    @Provides
    fun provideLogsParser(): LogsParser {
        return mock(LogsParser::class.java)
    }
}
