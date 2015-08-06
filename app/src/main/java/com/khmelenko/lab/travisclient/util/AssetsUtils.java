package com.khmelenko.lab.travisclient.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Gives util methods for working with Assets
 *
 * @author Dmytro Khmelenko
 */
public final class AssetsUtils {

    // denied constructor
    private AssetsUtils() {
    }

    /**
     * Gets properties from Assets
     *
     * @param name    Properties file
     * @param context Context
     * @return Properties
     */
    public static Properties getProperties(String name, Context context) {
        Resources resources = context.getResources();
        AssetManager assetManager = resources.getAssets();
        Properties properties = null;

        // Read from the /assets directory
        try {
            InputStream inputStream = assetManager.open(name);
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
