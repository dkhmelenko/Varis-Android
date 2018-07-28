package com.khmelenko.lab.varis.dagger.module


import android.content.Context

import com.khmelenko.lab.varis.storage.AppSettings
import com.khmelenko.lab.varis.storage.CacheStorage

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

/**
 * Storage module
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
object StorageModule {

    @Provides
    @Singleton
    fun provideCache(): CacheStorage {
        return CacheStorage.newInstance()
    }

    @Provides
    @Singleton
    fun provideAppSettings(context: Context): AppSettings {
        return AppSettings(context)
    }
}

