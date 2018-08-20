package com.khmelenko.lab.varis.dagger

import com.khmelenko.lab.varis.storage.AppSettings
import com.khmelenko.lab.varis.storage.CacheStorage
import dagger.Module
import dagger.Provides
import org.mockito.Mockito.mock
import javax.inject.Singleton

/**
 * Storage module for testing
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
class TestStorageModule {

    @Provides
    @Singleton
    fun provideCache(): CacheStorage {
        return mock(CacheStorage::class.java)
    }

    @Provides
    @Singleton
    fun provideAppSettings(): AppSettings {
        return mock(AppSettings::class.java)
    }
}
