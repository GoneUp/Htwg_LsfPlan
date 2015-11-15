package com.hstrobel.lsfplan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.List;

public class MainActivity extends AppCompatActivity {

    SharedPreferences mSettings;
    TextView infoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.mainDefaultFragment, new MainDefaultFragment(), "def")
                    .commit();
        }

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        infoText = (TextView) findViewById(R.id.txtInfo);

        Log.d("LSF", "onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

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
                List<VEvent> evs = CalenderUtils.GetNextEvent(Globals.myCal);
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


            if (Globals.myCal == null) {
                if (getFragmentManager().findFragmentByTag("list") != null) {
                    android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.mainListviewFragment, new MainDefaultFragment(), "def");
                    transaction.commit();
                    infoText = (TextView) findViewById(R.id.txtInfo);
                }

                infoText.setText(R.string.main_noCalender);
            } else {
                if (getFragmentManager().findFragmentByTag("def") != null) {
                    android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.mainDefaultFragment, new MainListFragment(), "list");
                    //transaction.commit();
                }
            }


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

        } catch (Exception ex) {
            Log.e("LSF", "FAIL onResume:\n " + ExceptionUtils.getCause(ex));
            Log.e("LSF", "FAIL onResume ST:\n " + ExceptionUtils.getFullStackTrace(ex));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LSF", "onDestroy");
        //Globals.Save();
    }
}

