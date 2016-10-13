package com.hstrobel.lsfplan.gui.download.network;

import android.os.Handler;
import android.util.Log;

import com.hstrobel.lsfplan.model.calender.CalenderValidator;

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
    private IDownloadCallback mSelector;
    private Handler mHandler;

    public ICSLoader(IDownloadCallback selector, Handler handler, String url) {
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