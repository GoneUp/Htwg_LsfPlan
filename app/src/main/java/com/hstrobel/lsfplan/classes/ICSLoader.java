package com.hstrobel.lsfplan.classes;

import android.os.Handler;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Henry on 04.12.2015.
 */
public class ICSLoader implements Runnable {
    public String file = "";
    public String url = "";
    public InputStream fileStream = null;
    private DownloadCallback mSelector;
    private Handler mHandler;

    public ICSLoader(DownloadCallback selector, Handler handler, String url) {
        this.url = url;
        mSelector = selector;
        mHandler = handler;
    }


    @Override
    public void run() {
        try {
            Log.i("LSF", "run: " + url);
            fileStream = new URL(url).openStream();
            file = IOUtils.toString(fileStream, "UTF-8");

            boolean ignoring = CalenderValidator.CorrectEvents();
            if (ignoring)
                Log.i("LSF", "doInBackground: ignored lectures");

            mSelector.FileLoaded();

        } catch (Exception ex) {
            System.out.println("FAIL DL:\n " + ExceptionUtils.getCause(ex));
            System.out.println("FAIL DL ST:\n " + ExceptionUtils.getFullStackTrace(ex));
            file = null;
        }
    }
}