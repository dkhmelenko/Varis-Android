package com.khmelenko.lab.travisclient.util;

import android.content.Context;

import com.khmelenko.lab.travisclient.TravisApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Provides utilities related with files
 *
 * @author Dmytro Khmelenko
 */
public final class FileUtils {

    private FileUtils() {

    }

    /**
     * Writes data to the file in internal memory
     *
     * @param fileName File name
     * @param data     Data for saving
     */
    public static void writeInternalFile(String fileName, String data) {
        try {
            Context context = TravisApp.getAppContext();
            FileOutputStream stream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(stream);
            outputWriter.write(data);
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads data from the file in internal memory
     *
     * @param fileName File name
     * @return Read data
     */
    public static String readInternalFile(String fileName) {
        String dataFromFile = "";

        Context context = TravisApp.getAppContext();
        File file = context.getFileStreamPath(fileName);
        if (file.exists()) {

            try {
                InputStream inputStream = context.openFileInput(fileName);
                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    dataFromFile = stringBuilder.toString();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        return dataFromFile;
    }

    /**
     * Deletes the file in internal memory
     *
     * @param fileName File name
     */
    public static void deleteInternalFile(String fileName) {
        try {
            Context context = TravisApp.getAppContext();
            context.deleteFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
