package com.manojkhannakm.gtavtrainer.net.client;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * @author Manoj Khanna
 */

public class Writer {

    private final BufferedWriter mWriter;

    public Writer(OutputStream outputStream) {
        mWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    public void writeLine(String line) {
        try {
            mWriter.write(line + "\n");
            mWriter.flush();
        } catch (IOException e) {
            Log.e(Writer.class.getName(), e.getMessage(), e);
        }
    }

    public void close() {
        try {
            mWriter.close();
        } catch (IOException e) {
            Log.e(Writer.class.getName(), e.getMessage(), e);
        }
    }

}
