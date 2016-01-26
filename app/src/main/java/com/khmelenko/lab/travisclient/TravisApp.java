package com.khmelenko.lab.travisclient;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.khmelenko.lab.travisclient.common.Constants;
import com.khmelenko.lab.travisclient.component.DaggerNetworkComponent;
import com.khmelenko.lab.travisclient.component.NetworkComponent;
import com.khmelenko.lab.travisclient.storage.AppSettings;

/**
 * Application class
 *
 * @author Dmytro Khmelenko
 */
public final class TravisApp extends Application {

    private static Context mContext;

    private NetworkComponent mNetworkComponent;

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        String server = AppSettings.getServerUrl();
        if (TextUtils.isEmpty(server)) {
            AppSettings.putServerType(0);
            AppSettings.putServerUrl(Constants.OPEN_SOURCE_TRAVIS_URL);
        }

        mNetworkComponent = DaggerNetworkComponent.create();
    }

    /**
     * Gets application context
     *
     * @return Application context
     */
    public static Context getAppContext() {
        return mContext;
    }

    /**
     * Gets network component
     *
     * @return Network component
     */
    public NetworkComponent getNetworkComponent() {
        return mNetworkComponent;
    }
}
