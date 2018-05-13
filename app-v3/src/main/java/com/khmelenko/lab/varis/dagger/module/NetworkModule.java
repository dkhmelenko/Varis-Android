package com.khmelenko.lab.varis.dagger.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.khmelenko.lab.varis.log.LogsParser;
import com.khmelenko.lab.varis.network.retrofit.ItemTypeAdapterFactory;
import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient;
import com.khmelenko.lab.varis.network.retrofit.raw.RawClient;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;
import com.khmelenko.lab.varis.storage.AppSettings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Network module
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
public abstract class NetworkModule {

    @Provides
    @Singleton
    public static TravisRestClient provideTravisRestClientRx(Retrofit retrofit, OkHttpClient okHttpClient, AppSettings appSettings) {
        return new TravisRestClient(retrofit, okHttpClient, appSettings);
    }

    @Provides
    @Singleton
    public static GitHubRestClient provideGitHubRestClientRx(Retrofit retrofit) {
        return new GitHubRestClient(retrofit);
    }

    @Provides
    @Singleton
    public static RawClient provideRawRestClientRx(Retrofit retrofit, OkHttpClient okHttpClient, AppSettings appSettings) {
        return new RawClient(retrofit, okHttpClient, appSettings);
    }

    @Provides
    @Singleton
    public static OkHttpClient okHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .followRedirects(false)
                .followSslRedirects(false)
                .build();
    }

    @Provides
    @Singleton
    public static Retrofit retrofit(OkHttpClient okHttpClient, AppSettings appSettings) {
        return new Retrofit.Builder()
                .baseUrl(appSettings.getServerUrl())
                .addConverterFactory(GsonConverterFactory.create(constructGsonConverter()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    public static LogsParser provideCommandsParser() {
        return new LogsParser();
    }

    /**
     * Construct Gson converter
     *
     * @return Gson converter
     */
    private static Gson constructGsonConverter() {
        return new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .create();
    }
}
