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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hstrobel.lsfplan.classes.CalenderValidator;
import com.hstrobel.lsfplan.classes.Globals;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.InputStream;
import java.net.URL;

public class WebSelector extends ActionBarActivity {

    private WebView webView;
    private SharedPreferences.Editor editor;
    private Handler  mHandler;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_selector);

        mHandler = new Handler();

        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

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
                spinner.setVisibility(View.VISIBLE);
            }
        });
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = mSettings.edit();
        String savedURL = mSettings.getString("URL", "missing");

        if (savedURL == "missing") {
            savedURL = "https://lsf.htwg-konstanz.de/qisserver/rds?state=verpublish&publishContainer=stgPlanList&navigationPosition=lectures%2CcurriculaschedulesList&breadcrumb=curriculaschedules&topitem=lectures&subitem=curriculaschedulesList";
            editor.putString("URL", savedURL);
            editor.commit();
        }

        webView.loadUrl(savedURL);

    }

    public void DownloadedICS() {
        try {
            if (!Globals.loader.file.startsWith("BEGIN:VCALENDAR")) {
                //not a ics file
                Snackbar.make(findViewById(android.R.id.content), R.string.webView_fileNotValid, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Globals.icsFileStream = null;
                spinner.setVisibility(View.GONE);
                return;
            }

            Globals.Update(this);
            Snackbar.make(findViewById(android.R.id.content), R.string.webView_fileLoaded, Snackbar.LENGTH_SHORT).show();

            //save it
            editor.putBoolean("gotICS", true);
            editor.putString("ICS_FILE",  Globals.icsFile);
           // editor.putString("URL", webView.getUrl());
            editor.commit();

            //navigate back to main
            NavUtils.navigateUpFromSameTask(this);

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "DL FAIL " , Toast.LENGTH_SHORT).show();
            Log.e("LSF", "FAIL DL:\n " + ExceptionUtils.getCause(ex));
            Log.e("LSF", "FAIL DL ST:\n " + ExceptionUtils.getFullStackTrace(ex));
            spinner.setVisibility(View.GONE);
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
        public String file = "";
        public InputStream fileStream = null;

        public ICSLoader() {}
        @Override
        protected String doInBackground(String... params) {
            // Making HTTP request
            try {
                fileStream = new URL(params[0]).openStream();
                file = IOUtils.toString(fileStream, "UTF-8");

                boolean ignoring = CalenderValidator.CorrectEvents();
                if (ignoring) DisplayTost("The file contained invalid lectures. The app ignored these.");

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
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }
}
