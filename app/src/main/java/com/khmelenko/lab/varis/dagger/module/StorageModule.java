package com.khmelenko.lab.varis.dagger.module;


import android.content.Context;

import com.khmelenko.lab.varis.storage.AppSettings;
import com.khmelenko.lab.varis.storage.CacheStorage;

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

    @Provides
    @Singleton
    public AppSettings provideAppSettings(Context context) {
        return new AppSettings(context);
    }
}

