package com.khmelenko.lab.varis

import android.app.Activity
import android.app.Application
import android.content.Context
import android.text.TextUtils
import com.khmelenko.lab.varis.common.Constants
import com.khmelenko.lab.varis.dagger.component.DaggerBaseComponent
import com.khmelenko.lab.varis.dagger.module.ApplicationModule
import com.khmelenko.lab.varis.dagger.module.NetworkModule
import com.khmelenko.lab.varis.dagger.module.StorageModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * Application class
 *
 * @author Dmytro Khmelenko
 */
class TravisApp : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        val baseComponent = DaggerBaseComponent.builder()
                .applicationModule(ApplicationModule)
                .networkModule(NetworkModule)
                .storageModule(StorageModule)
                .build()
        baseComponent.inject(this)

        val appSettings = baseComponent.appSettings()
        val server = appSettings.serverUrl
        if (TextUtils.isEmpty(server)) {
            appSettings.putServerType(0)
            appSettings.putServerUrl(Constants.OPEN_SOURCE_TRAVIS_URL)
        }
    }

    override fun activityInjector(): AndroidInjector<Activity>? {
        return dispatchingActivityInjector
    }

    companion object {

        /**
         * Gets application context
         *
         * @return Application context
         */
        @JvmStatic
        var appContext: Context? = null
            private set

        /**
         * Gets application instance
         *
         * @return Application instance
         */
        @JvmStatic
        fun instance(): TravisApp {
            return appContext as TravisApp
        }
    }
}
