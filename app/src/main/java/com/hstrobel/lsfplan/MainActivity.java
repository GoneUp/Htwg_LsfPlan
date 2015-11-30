package com.hstrobel.lsfplan;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.util.DebugUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.hstrobel.lsfplan.classes.CalenderUtils;
import com.hstrobel.lsfplan.classes.Globals;
import com.hstrobel.lsfplan.frags.MainDefaultFragment;
import com.hstrobel.lsfplan.frags.MainListFragment;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SharedPreferences mSettings;
    TextView infoText;
    Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.mainDefaultFragment, new MainDefaultFragment(), "def")
                    .commit();
        }
        */

        infoText = (TextView) findViewById(R.id.txtInfo);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        int starts = mSettings.getInt("starts", 0);
        starts++;
        mSettings.edit().putInt("starts", starts).apply(); // maybe for a rating dialog later


        if (shouldDisplayReloadDialog()) {
            DisplayDialog();
        }

        Log.d("LSF", "onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, UserSettings.class);
            startActivity(intent);
        } else if (id == R.id.action_setCalender) {
            Intent intent = new Intent(this, WebSelector.class);
            startActivity(intent);
        } else if (id == R.id.action_testNotfication) {
            if (Globals.myCal != null) {
                List<VEvent> evs = CalenderUtils.getNextEvent(Globals.myCal);
                for (VEvent ev : evs) {
                    CalenderUtils.showNotfication(ev, this);
                }
            } else {
                Toast.makeText(getApplicationContext(), "No plan, no notifications ;)", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check for calender
        //not present --> info text
        //present --> show info

        try {
            Globals.InitCalender(this, true);

            //Ads meh
            AdView mAdView = (AdView) findViewById(R.id.adView);
            if (mSettings.getBoolean("enableAds", false)) {
                mAdView.setVisibility(View.VISIBLE);
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice("2FF92E008889C6976B3F697DE3CB318A") //find 7
                        .build();
                mAdView.loadAd(adRequest);
            } else {
                mAdView.setVisibility(View.GONE);
            }

            if (Globals.myCal == null) {
                if (getFragmentManager().findFragmentByTag("list") != null) {
                    android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.mainListviewFragment, new MainDefaultFragment(), "def");
                    transaction.commit();
                    infoText = (TextView) findViewById(R.id.txtInfo);
                }

                infoText.setText(R.string.main_noCalender);
                mAdView.setVisibility(View.GONE); //disable ads on empty mode, too aggresive
            } else {
                if (getFragmentManager().findFragmentByTag("def") != null) {
                    android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.mainDefaultFragment, new MainListFragment(), "list");
                    //transaction.commit();
                }
            }



        } catch (Exception ex) {
            Log.e("LSF", "FAIL onResume:\n " + ExceptionUtils.getCause(ex));
            Log.e("LSF", "FAIL onResume ST:\n " + ExceptionUtils.getFullStackTrace(ex));
        }

    }

    private boolean shouldDisplayReloadDialog() {
        boolean enabled = mSettings.getBoolean("enableRefresh", true);
        if (!enabled) return false;

        GregorianCalendar now = new GregorianCalendar(); //one warning weekly
        int week_shown = mSettings.getInt("ICS_WEEK_SHOWN", -1);
        if (week_shown == now.get(Calendar.WEEK_OF_YEAR)) return false;

        long time_load = mSettings.getLong("ICS_DATE", Calendar.getInstance().getTimeInMillis());
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(time_load);
        cal.add(Calendar.WEEK_OF_YEAR, 4); //2 weeks reload timephase

        if (cal.before(now)) {
            Log.d("LSF", "displayReloadDialog: true # " + cal);
            return true;
        }
        return false;
    }

    private void DisplayDialog() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setTitle(R.string.main_refresh_title)
                .setMessage(R.string.main_refresh_body);

        builder.setPositiveButton(getString(R.string.main_button_reload), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onOptionsItemSelected(mMenu.findItem(R.id.action_setCalender));
            }
        });

        builder.setNegativeButton(getString(R.string.main_button_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();

        mSettings.edit().putInt("ICS_WEEK_SHOWN", new GregorianCalendar().get(Calendar.WEEK_OF_YEAR)).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LSF", "onDestroy");
        //Globals.Save();
    }
}

