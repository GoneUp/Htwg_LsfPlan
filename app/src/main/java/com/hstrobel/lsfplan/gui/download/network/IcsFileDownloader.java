package com.hstrobel.lsfplan.gui.download.network;

import android.content.Context;
import android.util.Log;

import com.hstrobel.lsfplan.model.Utils;
import com.hstrobel.lsfplan.model.calender.CalenderValidator;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Henry on 04.12.2015.
 */
public class IcsFileDownloader implements Runnable {
    private static final String TAG = "LSF";

    private String file = "";
    private String url = "";
    private IDownloadCallback downloadCallback;
    private Context context;

    public IcsFileDownloader(IDownloadCallback selector, String url, Context c) {
        this.url = url;
        downloadCallback = selector;
        context = c;
    }


    @Override
    public void run() {
        try {
            Log.i(TAG, "run: " + url);
            HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
            con.setSSLSocketFactory(Utils.generateSocketFactory(context));
            InputStream fileStream = con.getInputStream();

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