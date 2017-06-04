package com.khmelenko.lab.varis.dagger.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.khmelenko.lab.varis.network.retrofit.ItemTypeAdapterFactory;
import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClientRx;
import com.khmelenko.lab.varis.network.retrofit.raw.RawClientRx;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClientRx;
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
public class NetworkModule {

    @Provides
    @Singleton
    public TravisRestClientRx provideTravisRestClientRx(Retrofit retrofit, OkHttpClient okHttpClient) {
        return new TravisRestClientRx(retrofit, okHttpClient);
    }

    @Provides
    @Singleton
    public GitHubRestClientRx provideGitHubRestClientRx(Retrofit retrofit) {
        return new GitHubRestClientRx(retrofit);
    }

    @Provides
    @Singleton
    public RawClientRx provideRawRestClientRx(Retrofit retrofit, OkHttpClient okHttpClient) {
        return new RawClientRx(retrofit, okHttpClient);
    }

    @Provides
    @Singleton
    public OkHttpClient okHttpClient() {
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
    public Retrofit retrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(AppSettings.getServerUrl())
                .addConverterFactory(GsonConverterFactory.create(constructGsonConverter()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    /**
     * Construct Gson converter
     *
     * @return Gson converter
     */
    private Gson constructGsonConverter() {
        return new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .create();
    }
}
