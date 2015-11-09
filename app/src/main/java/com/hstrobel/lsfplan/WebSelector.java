package com.hstrobel.lsfplan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import org.w3c.dom.Document;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebSelector extends AppCompatActivity implements View.OnClickListener {

    private Button btnFoundPage;
    private WebView webView;
    SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = mSettings.edit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_selector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnFoundPage = (Button) findViewById(R.id.btnFoundPage);
        btnFoundPage.setOnClickListener(this);

        webView = (WebView) findViewById(R.id.webView);
        String savedURL = mSettings.getString("URL", "missing");

        if (savedURL == "missing"){
            savedURL = "https://lsf.htwg-konstanz.de/qisserver/rds?state=wplan&act=stg&pool=stg&show=plan&P.vx=kurz&r_zuordabstgv.semvonint=2&r_zuordabstgv.sembisint=2&missing=allTerms&k_parallel.parallelid=&k_abstgv.abstgvnr=4511&r_zuordabstgv.phaseid=";
            editor.putString("URL", savedURL);
            editor.commit();
        }

        webView.loadUrl(savedURL);

    }


    public void onClick(View v)
    {
        if (v == btnFoundPage)
        {
            //Document doc = Jsoup.connect("http://en.wikipedia.org/").get();
            Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        return;

    }

    public void getHtml(String url) {
        HttpURLConnection urlConnection = null;
        try {
        URL page = new URL("http://www.android.com/");
        urlConnection = (HttpURLConnection) page.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

        }
            catch(Exception ex){

            }
            finally {
                urlConnection.disconnect();
            }

    }


}
