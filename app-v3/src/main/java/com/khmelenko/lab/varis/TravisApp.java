package com.khmelenko.lab.varis;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.khmelenko.lab.varis.common.Constants;
import com.khmelenko.lab.varis.dagger.component.BaseComponent;
import com.khmelenko.lab.varis.dagger.component.DaggerBaseComponent;
import com.khmelenko.lab.varis.storage.AppSettings;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/**
 * Application class
 *
 * @author Dmytro Khmelenko
 */
public final class TravisApp extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> mDispatchingActivityInjector;

    private static Context sContext;

    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        BaseComponent baseComponent = DaggerBaseComponent.create();
        baseComponent.inject(this);

        AppSettings appSettings = baseComponent.appSettings();
        String server = appSettings.getServerUrl();
        if (TextUtils.isEmpty(server)) {
            appSettings.putServerType(0);
            appSettings.putServerUrl(Constants.OPEN_SOURCE_TRAVIS_URL);
        }
    }

    /**
     * Gets application context
     *
     * @return Application context
     */
    public static Context getAppContext() {
        return sContext;
    }

    /**
     * Gets application instance
     *
     * @return Application instance
     */
    public static TravisApp instance() {
        return (TravisApp) sContext;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return mDispatchingActivityInjector;
    }
}
