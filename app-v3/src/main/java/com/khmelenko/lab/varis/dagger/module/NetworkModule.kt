package com.khmelenko.lab.varis.dagger.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.khmelenko.lab.varis.log.LogsParser
import com.khmelenko.lab.varis.network.retrofit.ItemTypeAdapterFactory
import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient
import com.khmelenko.lab.varis.network.retrofit.raw.RawClient
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient
import com.khmelenko.lab.varis.storage.AppSettings

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Network module
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideTravisRestClientRx(retrofit: Retrofit, okHttpClient: OkHttpClient, appSettings: AppSettings): TravisRestClient {
        return TravisRestClient(retrofit, okHttpClient, appSettings)
    }

    @Provides
    @Singleton
    fun provideGitHubRestClientRx(retrofit: Retrofit): GitHubRestClient {
        return GitHubRestClient(retrofit)
    }

    @Provides
    @Singleton
    fun provideRawRestClientRx(retrofit: Retrofit, okHttpClient: OkHttpClient, appSettings: AppSettings): RawClient {
        return RawClient(retrofit, okHttpClient, appSettings)
    }

    @Provides
    @Singleton
    fun okHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .followRedirects(false)
                .followSslRedirects(false)
                .build()
    }

    @Provides
    @Singleton
    fun retrofit(okHttpClient: OkHttpClient, appSettings: AppSettings): Retrofit {
        return Retrofit.Builder()
                .baseUrl(appSettings.serverUrl)
                .addConverterFactory(GsonConverterFactory.create(constructGsonConverter()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
    }

    @Provides
    @Singleton
    fun provideCommandsParser(): LogsParser {
        return LogsParser()
    }

    /**
     * Construct Gson converter
     *
     * @return Gson converter
     */
    private fun constructGsonConverter(): Gson {
        return GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .registerTypeAdapterFactory(ItemTypeAdapterFactory())
                .create()
    }
}
