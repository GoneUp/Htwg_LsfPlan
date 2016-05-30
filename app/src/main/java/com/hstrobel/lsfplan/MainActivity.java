package com.hstrobel.lsfplan;

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

    public MainDefaultFragment mDefFragment;
    public MainListFragment mListFragment;
    private SharedPreferences mSettings;
    private TextView infoText;
    private Menu mMenu;
    private AdView mAdView;

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

        //Own Init
        infoText = (TextView) findViewById(R.id.txtInfo);
        mAdView = (AdView) findViewById(R.id.adView);
        mDefFragment = (MainDefaultFragment) getFragmentManager().findFragmentById(R.id.mainDefaultFragment);
        mListFragment = (MainListFragment)  getFragmentManager().findFragmentById(R.id.mainListFragment);
        if (mDefFragment.getView() != null) mDefFragment.getView().setVisibility(View.GONE);
        if (mListFragment.getView() != null )mListFragment.getView().setVisibility(View.GONE);

        Globals.mainActivity = this;

        //Settings
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        int starts = mSettings.getInt("starts", 0);
        String savedURL = mSettings.getString("URL", "missing");

        starts++;

        SharedPreferences.Editor editor =  mSettings.edit();
        editor.putInt("starts", starts); // maybe for a rating dialog later

        if (savedURL == "missing") {
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
            if (mSettings.getBoolean("enableOldDL", false)){
                Intent intent = new Intent(this, WebSelector.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, HtmlWebSelector.class);
                startActivity(intent);
            }
        } else if (id == R.id.action_testNotfication) {
            if (Globals.myCal != null) {
                List<VEvent> evs = CalenderUtils.getNextEvent(Globals.myCal);
                for (VEvent ev : evs) {
                    CalenderUtils.showNotification(ev, this);
                }
            } else {
                Toast.makeText(getApplicationContext(), "No plan, no notifications ;)", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_reset) {
            if (mListFragment != null){
                mListFragment.onDateReset();
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
                if (mDefFragment.getView() != null) mDefFragment.getView().setVisibility(View.VISIBLE);
                if (mListFragment.getView() != null )mListFragment.getView().setVisibility(View.GONE);

                infoText.setText(R.string.main_noCalender);
                mAdView.setVisibility(View.GONE); //disable ads on empty mode, too aggresive
            } else {
                if (mDefFragment.getView() != null) mDefFragment.getView().setVisibility(View.GONE);
                if (mListFragment.getView() != null )mListFragment.getView().setVisibility(View.VISIBLE);
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

