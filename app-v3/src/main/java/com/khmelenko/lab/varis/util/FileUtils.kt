package com.khmelenko.lab.varis.util

import android.content.Context
import com.khmelenko.lab.varis.TravisApp
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * Provides utilities related with files
 *
 * @author Dmytro Khmelenko
 */
object FileUtils {

    /**
     * Writes data to the file in internal memory
     *
     * @param fileName Filename
     * @param data     Data for saving
     */
    fun writeInternalFile(fileName: String, data: String) {
        val context = TravisApp.appContext
        writeInternalFile(fileName, data, context)
    }

    /**
     * Writes data to the file in internal memory
     *
     * @param fileName File name
     * @param data     Data for saving
     * @param context  Context
     */
    fun writeInternalFile(fileName: String, data: String, context: Context?) {
        try {
            val stream = context!!.openFileOutput(fileName, Context.MODE_PRIVATE)
            val outputWriter = OutputStreamWriter(stream)
            outputWriter.write(data)
            outputWriter.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Reads data from the file in internal memory
     *
     * @param fileName Filename
     * @return File content
     */
    fun readInternalFile(fileName: String): String {
        val context = TravisApp.appContext
        return readInternalFile(fileName, context!!)
    }

    /**
     * Reads data from the file in internal memory
     *
     * @param fileName File name
     * @param context  Context
     * @return Read data
     */
    fun readInternalFile(fileName: String, context: Context): String {
        var dataFromFile = ""

        val file = context.getFileStreamPath(fileName)
        if (file.exists()) {

            try {
                val inputStream = context.openFileInput(fileName)
                if (inputStream != null) {
                    val inputStreamReader = InputStreamReader(inputStream)
                    val bufferedReader = BufferedReader(inputStreamReader)
                    val stringBuilder = StringBuilder()

                    bufferedReader.forEachLine {
                        stringBuilder.append(it)
                    }

                    inputStream.close()
                    dataFromFile = stringBuilder.toString()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        return dataFromFile
    }

    /**
     * Deletes the file in internal memory
     *
     * @param fileName Filename
     */
    fun deleteInternalFile(fileName: String) {
        val context = TravisApp.appContext
        deleteInternalFile(fileName, context)
    }

    /**
     * Deletes the file in internal memory
     *
     * @param fileName File name
     * @param context  Context
     */
    fun deleteInternalFile(fileName: String, context: Context?) {
        try {
            context!!.deleteFile(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
