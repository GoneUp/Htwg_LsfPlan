package com.hstrobel.lsfplan;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.hstrobel.lsfplan.classes.Globals;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.InputStream;
import java.net.URL;

public class WebSelector extends ActionBarActivity {

    private WebView webView;
    SharedPreferences mSettings;
    SharedPreferences.Editor editor;
    Handler  mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_selector);

        mHandler = new Handler();

        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println(url);
                if (Uri.parse(url).getHost().endsWith("https://lsf.htwg-konstanz.de/qisserver/rds?state=verpublish")) {
                    //trying to acces a file
                    DisplayTost(getString(R.string.webView_fileLoading));
                }
                return false;
            }
        });
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DisplayTost(getString(R.string.webView_fileLoading));
                Globals.loader = new ICSLoader();
                Globals.loader.execute(url);
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

    public void DownloadedICS() {
        try {
            if (! Globals.loader.file.startsWith("BEGIN:VCALENDAR")) {
                //not a ics file
                Snackbar.make(findViewById(android.R.id.content), R.string.webView_fileNotValid, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return;
            }

            Snackbar.make(findViewById(android.R.id.content), R.string.webView_fileLoaded, Snackbar.LENGTH_SHORT).show();

            Globals.icsFile =  Globals.loader.file;
            Globals.icsFileStream =  Globals.loader.fileStream;
            Globals.Update(this);

            //save it
            editor.putBoolean("gotICS", true);
            editor.putString("ICS_FILE",  Globals.icsFile);
            editor.putString("URL", webView.getUrl());
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

    public class ICSLoader extends AsyncTask<String, String, String> {
        String file = "";
        InputStream fileStream = null;

        public ICSLoader() {}
        @Override
        protected String doInBackground(String... params) {
            // Making HTTP request
            try {
                fileStream = new URL(params[0]).openStream();
                file = IOUtils.toString(fileStream, "UTF-8");

            } catch (Exception ex) {
                System.out.println("FAIL DL:\n " + ExceptionUtils.getCause(ex));
                System.out.println("FAIL DL ST:\n " + ExceptionUtils.getFullStackTrace(ex));
            }
            return file;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            DownloadedICS();
        }
    }

    protected void DisplayTost(final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
