package com.khmelenko.lab.travisclient.module;

import com.khmelenko.lab.travisclient.network.retrofit.RestClient;

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
    public RestClient provideRestClient() {
        return RestClient.getInstance();
    }
}
