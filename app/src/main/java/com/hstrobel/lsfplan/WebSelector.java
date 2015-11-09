package com.hstrobel.lsfplan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebSelector extends AppCompatActivity implements View.OnClickListener {

    private Button btnFoundPage;
    private WebView webView;
    SharedPreferences mSettings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_selector);

        btnFoundPage = (Button) findViewById(R.id.btnFoundPage);
        btnFoundPage.setOnClickListener(this);

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


    public void onClick(View v) {
        if (v == btnFoundPage) {
            //Document doc = Jsoup.connect("http://en.wikipedia.org/").get();
            Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        return;

    }


    public void DownloadICS(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL page = new URL(url);
            urlConnection = (HttpURLConnection) page.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String icsFile = convertStreamToString(in);

            Toast.makeText(getApplicationContext(), "Downloaded ICS FILE!!!!", Toast.LENGTH_SHORT).show();

        } catch (Exception ex) {

        } finally {
            urlConnection.disconnect();
        }

    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
