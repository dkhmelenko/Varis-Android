package com.khmelenko.lab.varis.dagger.module

import android.content.Context
import com.khmelenko.lab.varis.TravisApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Application module
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module
object ApplicationModule {

    @Provides
    @Singleton
    fun provideAppContext(): Context = TravisApp.appContext!!
}
