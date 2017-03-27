package com.hstrobel.lsfplan.gui.download.network;

import android.util.Log;

import com.hstrobel.lsfplan.model.Utils;
import com.hstrobel.lsfplan.model.calender.CalenderValidator;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Henry on 04.12.2015.
 */
public class IcsFileDownloader implements Runnable {
    private static final String TAG = "LSF";

    private String file = "";
    private String url = "";
    private IDownloadCallback downloadCallback;

    public IcsFileDownloader(IDownloadCallback selector, String url) {
        this.url = url;
        downloadCallback = selector;
    }


    @Override
    public void run() {
        try {
            Log.i(TAG, "run: " + url);
            InputStream fileStream = new URL(url).openStream();
            file = Utils.streamToString(fileStream, "UTF-8");
            file = CalenderValidator.CorrectEvents(file);

            downloadCallback.FileLoaded();

        } catch (Exception ex) {
            Log.e(TAG, "FAIL DL: ", ex);
            file = null;
        }
    }

    public String getUrl() {
        return url;
    }

    public String getFile() {
        return file;
    }
}