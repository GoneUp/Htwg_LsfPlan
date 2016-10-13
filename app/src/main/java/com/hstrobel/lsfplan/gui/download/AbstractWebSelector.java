package com.hstrobel.lsfplan.gui.download;

import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hstrobel.lsfplan.Globals;
import com.hstrobel.lsfplan.R;
import com.hstrobel.lsfplan.gui.download.network.IDownloadCallback;

import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * Created by Henry on 04.12.2015.
 */
public abstract class AbstractWebSelector extends AppCompatActivity implements IDownloadCallback {
    protected ProgressBar spinner;

    public void FileLoaded() {
        try {
            Looper.prepare();
            if (Globals.isDownloadValid()) {
                //not a ics file
                Snackbar.make(findViewById(android.R.id.content), R.string.webView_fileNotValid, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                spinner.setVisibility(View.GONE);
                return;
            }

            Globals.SetNewCalendar(this);

            //navigate back to main
            Snackbar.make(findViewById(android.R.id.content), R.string.webView_fileLoaded, Snackbar.LENGTH_SHORT).show();
            NavUtils.navigateUpFromSameTask(this);

        } catch (Exception ex) {
            Toast.makeText(this, "DL FAIL ", Toast.LENGTH_SHORT).show();
            Log.e("LSF", "FAIL DL:\n " + ExceptionUtils.getCause(ex));
            Log.e("LSF", "FAIL DL ST:\n " + ExceptionUtils.getFullStackTrace(ex));
            spinner.setVisibility(View.GONE);
        }
    }

}
