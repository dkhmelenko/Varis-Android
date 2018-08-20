package com.khmelenko.lab.varis

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import com.khmelenko.lab.varis.util.AssetsUtils
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException
import java.io.InputStream

/**
 * Testing AssetsUtils class
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
class TestAssetsUtils {

    @Test
    @Throws(IOException::class)
    fun testGetProperties() {
        val property = "test"

        val context = mock(Context::class.java)
        val resources = mock(Resources::class.java)
        val assetManager = mock(AssetManager::class.java)
        val inputStream = mock(InputStream::class.java)
        whenever(context.resources).thenReturn(resources)
        whenever(resources.assets).thenReturn(assetManager)
        whenever(assetManager.open(property)).thenReturn(inputStream)

        AssetsUtils.getProperties(property, context)

        verify(context).resources
        verify(resources).assets
        verify(assetManager).open(property)
    }
}
