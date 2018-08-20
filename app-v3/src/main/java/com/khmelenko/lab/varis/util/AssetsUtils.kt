package com.khmelenko.lab.varis.util

import android.content.Context
import java.io.IOException
import java.util.Properties

/**
 * Gives util methods for working with Assets
 *
 * @author Dmytro Khmelenko
 */
object AssetsUtils {

    /**
     * Gets properties from Assets
     *
     * @param name    Properties file
     * @param context Context
     * @return Properties
     */
    fun getProperties(name: String, context: Context): Properties? {
        val resources = context.resources
        val assetManager = resources.assets
        var properties: Properties? = null

        // Read from the /assets directory
        try {
            val inputStream = assetManager.open(name)
            properties = Properties()
            properties.load(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return properties
    }
}
