package com.hstrobel.lsfplan.gui.download;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hstrobel.lsfplan.Globals;
import com.hstrobel.lsfplan.R;
import com.hstrobel.lsfplan.gui.download.network.ICSLoader;

public class WebviewSelector extends AbstractWebSelector {

    private WebviewSelector local;
    private WebView webView;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_selector);

        local = this;
        mHandler = new Handler();

        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("LSF", url);
                if (url.startsWith("https://lsf.htwg-konstanz.de/qisserver/rds?state=verpublish&status=transform")) {
                    //trying to acces a file
                    DisplayTost(getString(R.string.webView_fileLoading));
                    spinner.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Log.d("LSF", "setDownloadListener");
                Globals.icsLoader = new ICSLoader(local, url);
                new Thread(Globals.icsLoader).start();
            }
        });
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        String savedURL = Globals.settings.getString("URL", "missing");
        webView.loadUrl(savedURL);

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

    @Override
    protected void onStop() {
        super.onStop();

        String url = webView.getUrl();
        if (!TextUtils.isEmpty(url)) {
            SharedPreferences.Editor editor = Globals.settings.edit();
            editor.putString("URL", url);
            editor.apply();
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
