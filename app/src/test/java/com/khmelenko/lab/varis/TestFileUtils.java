package com.khmelenko.lab.varis;

import android.content.Context;

import com.khmelenko.lab.varis.util.FileUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for FileUtils
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TestFileUtils {

    @Before
    public void setupMock() {

    }

    @Test
    public void testReadFile() throws Exception {
        final String fileName = "test.txt";

        Context context = mock(Context.class);
        File file = mock(File.class);
        FileInputStream stream = mock(FileInputStream.class);
        when(context.getFileStreamPath(fileName)).thenReturn(file);
        when(file.exists()).thenReturn(true);
        when(context.openFileInput(fileName)).thenReturn(stream);

        FileUtils.readInternalFile(fileName, context);

        verify(context).getFileStreamPath(fileName);
        verify(context).openFileInput(fileName);
    }

    @Test
    public void testWriteFile() throws Exception {
        final String fileName = "test.txt";
        final String body = "content";
        final int mode = Context.MODE_PRIVATE;

        Context context = mock(Context.class);
        FileOutputStream stream = mock(FileOutputStream.class);
        when(context.openFileOutput(fileName, mode)).thenReturn(stream);

        FileUtils.writeInternalFile(fileName, body, context);

        verify(context).openFileOutput(fileName, mode);
    }

    @Test
    public void testDeleteFile() throws Exception {
        final String fileName = "test.txt";
        Context context = mock(Context.class);

        FileUtils.deleteInternalFile(fileName, context);

        verify(context).deleteFile(fileName);
    }

}
