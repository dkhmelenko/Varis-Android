package com.khmelenko.lab.varis.dagger;

/**
 *
 */

import com.khmelenko.lab.varis.storage.CacheStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.spy;

/**
 * Storage module for testing
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
public class TestStorageModule {

    @Provides
    @Singleton
    public CacheStorage provideCache() {
        return spy(CacheStorage.newInstance());
    }
}
