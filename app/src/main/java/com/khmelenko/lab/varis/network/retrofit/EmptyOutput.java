package com.khmelenko.lab.varis.network.retrofit;

import java.io.IOException;
import java.io.OutputStream;

import retrofit.mime.TypedOutput;

/**
 * Defines an empty output
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public final class EmptyOutput implements TypedOutput {

    public static final TypedOutput INSTANCE = new EmptyOutput();

    private EmptyOutput() {
    }

    @Override
    public String fileName() {
        return null;
    }

    @Override
    public String mimeType() {
        return "application/json";
    }

    @Override
    public long length() {
        return 0;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {

    }
}
