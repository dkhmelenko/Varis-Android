package com.khmelenko.lab.varis;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.khmelenko.lab.varis.common.Constants;
import com.khmelenko.lab.varis.dagger.component.ActivityInjectionComponent;
import com.khmelenko.lab.varis.dagger.component.BaseComponent;
import com.khmelenko.lab.varis.dagger.component.DaggerActivityInjectionComponent;
import com.khmelenko.lab.varis.dagger.component.DaggerBaseComponent;
import com.khmelenko.lab.varis.storage.AppSettings;

/**
 * Application class
 *
 * @author Dmytro Khmelenko
 */
public final class TravisApp extends Application {

    private static Context sContext;

    private ActivityInjectionComponent mActivityInjection;

    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        BaseComponent baseComponent = DaggerBaseComponent.create();

        mActivityInjection = DaggerActivityInjectionComponent.builder()
                .baseComponent(baseComponent)
                .build();

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

    /**
     * Gets activity injection component
     *
     * @return Activity injection component
     */
    public ActivityInjectionComponent activityInjector() {
        return mActivityInjection;
    }
}
