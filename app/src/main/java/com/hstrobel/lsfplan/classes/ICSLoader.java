package com.hstrobel.lsfplan.classes;

import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.hstrobel.lsfplan.frags.AbstractWebSelector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Henry on 04.12.2015.
 */
public class ICSLoader extends AsyncTask<Object, String, String> {
    public String file = "";
    public InputStream fileStream = null;
    private AbstractWebSelector mSelector;
    private Handler mHandler;

    public ICSLoader(AbstractWebSelector selector, Handler handler) {
        mSelector = selector;
        mHandler = handler;
    }


    @Override
    protected String doInBackground(Object... params) {
        try {
            fileStream = new URL(params[0].toString()).openStream();
            file = IOUtils.toString(fileStream, "UTF-8");

            boolean ignoring = CalenderValidator.CorrectEvents();
            if (ignoring)
                DisplayTost("The file contained invalid lectures. The app ignored these.");

        } catch (Exception ex) {
            System.out.println("FAIL DL:\n " + ExceptionUtils.getCause(ex));
            System.out.println("FAIL DL ST:\n " + ExceptionUtils.getFullStackTrace(ex));
            file = null;
        }
        return file;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mSelector.DownloadedICS();
    }

    protected void DisplayTost(final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mSelector, text, Toast.LENGTH_LONG).show();
            }
        });
    }
}