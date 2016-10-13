package com.hstrobel.lsfplan.gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.hstrobel.lsfplan.Globals;
import com.hstrobel.lsfplan.R;
import com.hstrobel.lsfplan.gui.download.HtmlWebSelector;
import com.hstrobel.lsfplan.gui.download.WebviewSelector;
import com.hstrobel.lsfplan.gui.eventlist.MainListFragment;
import com.hstrobel.lsfplan.model.NotificationUtils;
import com.hstrobel.lsfplan.model.calender.CalenderUtils;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public MainDefaultFragment defaultFragment;
    public MainListFragment listFragment;
    private SharedPreferences preferences;
    private TextView infoText;

    private AdView adView;
    private AdListener adListener = new AdListener() {
        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            //hide ad
            adView.setVisibility(View.GONE);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.title_activity_main);

        //Ads
        MobileAds.initialize(getApplicationContext(), getString(R.string.firebase_id));
        MobileAds.setAppMuted(true);
        adView = (AdView) findViewById(R.id.adView);

        //Own Init
        infoText = (TextView) findViewById(R.id.txtInfo);
        defaultFragment = (MainDefaultFragment) getFragmentManager().findFragmentById(R.id.mainDefaultFragment);
        listFragment = (MainListFragment) getFragmentManager().findFragmentById(R.id.mainListFragment);
        if (defaultFragment.getView() != null) defaultFragment.getView().setVisibility(View.GONE);
        if (listFragment.getView() != null) listFragment.getView().setVisibility(View.GONE);

        Globals.mainActivity = this;

        //Settings
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int starts = preferences.getInt("starts", 0);
        String savedURL = preferences.getString("URL", "missing");

        starts++;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("starts", starts); // maybe for a rating dialog later

        if (savedURL.equals("missing")) {
            savedURL = "https://lsf.htwg-konstanz.de/qisserver/rds?state=verpublish&publishContainer=stgPlanList&navigationPosition=lectures%2CcurriculaschedulesList&breadcrumb=curriculaschedules&topitem=lectures&subitem=curriculaschedulesList";
            editor.putString("URL", savedURL);
        }
        editor.apply();

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
            if (preferences.getBoolean("enableOldDL", false)) {
                Intent intent = new Intent(this, WebviewSelector.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, HtmlWebSelector.class);
                startActivity(intent);
            }
        } else if (id == R.id.action_testNotfication) {
            if (Globals.myCal != null) {
                List<VEvent> evs = CalenderUtils.getNextEvent(Globals.myCal);
                for (VEvent ev : evs) {
                    NotificationUtils.showNotification(ev, this);
                }
            } else {
                Toast.makeText(getApplicationContext(), "No plan, no notifications ;)", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_reset) {
            if (listFragment != null) {
                listFragment.onDateReset();
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
            if (preferences.getBoolean("enableAds", false)) {
                adView.setVisibility(View.VISIBLE);
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice("2FF92E008889C6976B3F697DE3CB318A") //find 7
                        .addTestDevice("E624D76F3DFE84D3E8E20B6C33C4A7C5")
                        .build();
                adView.setAdListener(adListener);
                adView.loadAd(adRequest);
            } else {
                adView.setVisibility(View.GONE);
            }


            if (Globals.myCal == null) {
                if (defaultFragment.getView() != null)
                    defaultFragment.getView().setVisibility(View.VISIBLE);
                if (listFragment.getView() != null) listFragment.getView().setVisibility(View.GONE);

                infoText.setText(R.string.main_noCalender);
                adView.setVisibility(View.GONE); //disable ads on empty mode, too aggresive
            } else {
                if (defaultFragment.getView() != null)
                    defaultFragment.getView().setVisibility(View.GONE);
                if (listFragment.getView() != null)
                    listFragment.getView().setVisibility(View.VISIBLE);
            }



        } catch (Exception ex) {
            Log.e("LSF", "FAIL onResume:\n " + ExceptionUtils.getMessage(ex));
            Log.e("LSF", "FAIL onResume ST:\n " + ExceptionUtils.getFullStackTrace(ex));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LSF", "onDestroy");
        //Globals.Save(); //no changes yet
    }


}

