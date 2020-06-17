package com.hstrobel.lsfplan.gui.download;

import android.os.Looper;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hstrobel.lsfplan.GlobalState;
import com.hstrobel.lsfplan.R;
import com.hstrobel.lsfplan.gui.download.network.IDownloadCallback;

/**
 * Created by Henry on 04.12.2015.
 */
public abstract class AbstractWebSelector extends AppCompatActivity implements IDownloadCallback {
    protected ProgressBar spinner;

    public void FileLoaded() {
        try {
            Looper.prepare();
            GlobalState state = GlobalState.getInstance();

            if (state.isDownloadInvalid()) {
                //not a ics file
                Snackbar.make(findViewById(android.R.id.content), R.string.webView_fileNotValid, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                spinner.setVisibility(View.GONE);
                return;
            }

            state.SetNewCalendar(this);

            //navigate back to main
            Snackbar.make(findViewById(android.R.id.content), R.string.webView_fileLoaded, Snackbar.LENGTH_SHORT).show();
            NavUtils.navigateUpFromSameTask(this);

        } catch (Exception ex) {
            Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show();
            Log.e("LSF", "DL: FileLoaded: ", ex);
            spinner.setVisibility(View.GONE);
        }
    }

}
