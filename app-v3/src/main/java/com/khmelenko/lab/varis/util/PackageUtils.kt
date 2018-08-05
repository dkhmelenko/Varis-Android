package com.khmelenko.lab.varis.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

import com.khmelenko.lab.varis.TravisApp

/**
 * Provides package related functionality
 *
 * @author Dmytro Khmelenko
 */
object PackageUtils {

    /**
     * Gets application version
     *
     * @return Application version
     */
    val appVersion: String
        get() {
            val context = TravisApp.appContext
            var versionName = ""
            try {
                val packageInfo = context?.packageManager?.getPackageInfo(context.packageName, 0)
                versionName = packageInfo?.versionName ?: ""
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return versionName
        }
}
