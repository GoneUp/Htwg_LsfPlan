package com.hstrobel.lsfplan.gui.download.network;

import android.util.Log;

import com.hstrobel.lsfplan.model.Utils;
import com.hstrobel.lsfplan.model.calender.CalenderValidator;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Henry on 04.12.2015.
 */
public class ICSLoader implements Runnable {
    private static final String TAG = "LSF";

    public String file = "";
    public String url = "";
    private IDownloadCallback downloadCallback;

    public ICSLoader(IDownloadCallback selector, String url) {
        this.url = url;
        downloadCallback = selector;
    }


    @Override
    public void run() {
        try {
            Log.i(TAG, "run: " + url);
            InputStream fileStream = new URL(url).openStream();
            file = Utils.streamToString(fileStream, "UTF-8");

            boolean ignoring = CalenderValidator.CorrectEvents();
            if (ignoring)
                Log.i(TAG, "doInBackground: ignored lectures");

            downloadCallback.FileLoaded();

        } catch (Exception ex) {
            Log.e(TAG, "FAIL DL: ", ex);
            file = null;
        }
    }


}