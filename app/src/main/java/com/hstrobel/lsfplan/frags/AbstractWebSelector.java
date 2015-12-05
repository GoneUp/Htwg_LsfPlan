package com.hstrobel.lsfplan.frags;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hstrobel.lsfplan.R;
import com.hstrobel.lsfplan.classes.Globals;

import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Calendar;

/**
 * Created by Henry on 04.12.2015.
 */
public abstract class AbstractWebSelector extends AppCompatActivity {
    protected ProgressBar spinner;

    public void DownloadedICS() {
        try {
            if (Globals.loader.file == null || !Globals.loader.file.startsWith("BEGIN:VCALENDAR")) {
                //not a ics file
                Snackbar.make(findViewById(android.R.id.content), R.string.webView_fileNotValid, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Globals.icsFileStream = null;
                spinner.setVisibility(View.GONE);
                return;
            }

            Globals.Update(this);
            Snackbar.make(findViewById(android.R.id.content), R.string.webView_fileLoaded, Snackbar.LENGTH_SHORT).show();

            //save it
            SharedPreferences.Editor editor = Globals.mSettings.edit();
            editor.putBoolean("gotICS", true);
            editor.putString("ICS_FILE", Globals.icsFile);
            editor.putLong("ICS_DATE", Calendar.getInstance().getTimeInMillis());
            // editor.putString("URL", webView.getUrl());
            editor.commit();

            //navigate back to main
            NavUtils.navigateUpFromSameTask(this);

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "DL FAIL ", Toast.LENGTH_SHORT).show();
            Log.e("LSF", "FAIL DL:\n " + ExceptionUtils.getCause(ex));
            Log.e("LSF", "FAIL DL ST:\n " + ExceptionUtils.getFullStackTrace(ex));
            spinner.setVisibility(View.GONE);
        }
    }

}
