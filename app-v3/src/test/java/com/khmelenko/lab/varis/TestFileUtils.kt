package com.khmelenko.lab.varis

import android.content.Context

import com.khmelenko.lab.varis.util.FileUtils
import com.nhaarman.mockito_kotlin.whenever

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

/**
 * Tests for FileUtils
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class TestFileUtils {

    @Test
    fun testReadFile() {
        val fileName = "test.txt"

        val context = mock(Context::class.java)
        val file = mock(File::class.java)
        val stream = mock(FileInputStream::class.java)
        whenever(context.getFileStreamPath(fileName)).thenReturn(file)
        whenever(file.exists()).thenReturn(true)
        whenever(context.openFileInput(fileName)).thenReturn(stream)

        FileUtils.readInternalFile(fileName, context)

        verify(context).getFileStreamPath(fileName)
        verify(context).openFileInput(fileName)
    }

    @Test
    fun testWriteFile() {
        val fileName = "test.txt"
        val body = "content"
        val mode = Context.MODE_PRIVATE

        val context = mock(Context::class.java)
        val stream = mock(FileOutputStream::class.java)
        whenever(context.openFileOutput(fileName, mode)).thenReturn(stream)

        FileUtils.writeInternalFile(fileName, body, context)

        verify(context).openFileOutput(fileName, mode)
    }

    @Test
    fun testDeleteFile() {
        val fileName = "test.txt"
        val context = mock(Context::class.java)

        FileUtils.deleteInternalFile(fileName, context)

        verify(context).deleteFile(fileName)
    }

}
