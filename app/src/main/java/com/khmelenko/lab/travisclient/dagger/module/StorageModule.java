package com.khmelenko.lab.travisclient.dagger.module;


import com.khmelenko.lab.travisclient.storage.CacheStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Storage module
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
public class StorageModule {

    @Provides
    @Singleton
    public CacheStorage provideCache() {
        return CacheStorage.newInstance();
    }
}

