package com.khmelenko.lab.varis.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.khmelenko.lab.varis.TravisApp;

/**
 * Provides package related functionality
 *
 * @author Dmytro Khmelenko
 */
public final class PackageUtils {

    private PackageUtils() {
    }

    /**
     * Gets application version
     *
     * @return Application version
     */
    public static String getAppVersion() {
        Context context = TravisApp.getAppContext();
        String versionName = "";
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
