package com.gtavtrainer.net.client;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Manoj Khanna
 */

public class Reader {

    private final BufferedReader mReader;

    public Reader(InputStream inputStream) {
        mReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public String readLine() {
        try {
            return mReader.readLine();
        } catch (IOException e) {
            Log.e(Reader.class.getName(), e.getMessage(), e);
        }

        return null;
    }

    public void close() {
        try {
            mReader.close();
        } catch (IOException e) {
            Log.e(Reader.class.getName(), e.getMessage(), e);
        }
    }

}
