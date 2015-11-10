package com.hstrobel.lsfplan;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebSelector extends ActionBarActivity {

    private WebView webView;
    SharedPreferences mSettings;
    SharedPreferences.Editor editor;
    public String icsFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_selector);

        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                //ics download lands here
                DownloadICS(url);
            }
        });

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = mSettings.edit();
        String savedURL = mSettings.getString("URL", "missing");

        if (savedURL == "missing") {
            savedURL = "https://lsf.htwg-konstanz.de/qisserver/rds?state=wplan&act=stg&pool=stg&show=plan&P.vx=kurz&r_zuordabstgv.semvonint=2&r_zuordabstgv.sembisint=2&missing=allTerms&k_parallel.parallelid=&k_abstgv.abstgvnr=4511&r_zuordabstgv.phaseid=";
            editor.putString("URL", savedURL);
            editor.commit();
        }

        webView.loadUrl(savedURL);

    }

    public void DownloadICS(String url) {
        try {
            new ICSLoader().execute(url);
            icsFile = ICSLoader.file;

            if (!icsFile.startsWith("BEGIN:VCALENDAR")) {
                //not a ics file
                Snackbar.make(findViewById(android.R.id.content), R.string.webView_fileNotValid, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return;
            }

            Toast.makeText(getApplicationContext(), R.string.webView_fileLoaded, Toast.LENGTH_SHORT).show();

            //save it
            editor.putBoolean("gotICS", true);
            editor.putString("ICS_FILE", icsFile);
            editor.commit();

            //navigate back to main
            NavUtils.navigateUpFromSameTask(this);

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "DL FAIL " , Toast.LENGTH_SHORT).show();
            System.out.println("FAIL DL:\n " + ExceptionUtils.getCause(ex));
            System.out.println("FAIL DL ST:\n " + ExceptionUtils.getFullStackTrace(ex));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }

        // Otherwise defer to system default behavior.
        super.onBackPressed();
    }

    public static class ICSLoader extends AsyncTask<String, String, String> {
        static String file = "";
        public ICSLoader() {}
        @Override
        protected String doInBackground(String... params) {
            // Making HTTP request
            try {
                Connection urlConnection = Jsoup.connect(params[0]);
                file =  urlConnection.get().text();

            } catch (Exception ex) {
                System.out.println("FAIL DL:\n " + ExceptionUtils.getCause(ex));
                System.out.println("FAIL DL ST:\n " + ExceptionUtils.getFullStackTrace(ex));
            }
            return file;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
