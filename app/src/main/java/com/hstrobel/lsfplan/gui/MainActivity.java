package com.hstrobel.lsfplan.gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hstrobel.lsfplan.GlobalState;
import com.hstrobel.lsfplan.R;
import com.hstrobel.lsfplan.gui.download.NativeSelector;
import com.hstrobel.lsfplan.gui.download.WebviewSelector;
import com.hstrobel.lsfplan.gui.eventlist.MainListFragment;
import com.hstrobel.lsfplan.gui.settings.UserSettings;
import com.hstrobel.lsfplan.model.NotificationUtils;
import com.hstrobel.lsfplan.model.calender.CalenderUtils;

import net.fortuna.ical4j.model.component.VEvent;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IOpenDownloader {
    private static final String TAG = "LSF";

    public MainDefaultFragment defaultFragment;
    public MainListFragment listFragment;
    private SharedPreferences preferences;
    private GlobalState state = GlobalState.getInstance();
    private Menu optionsMenu;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.title_activity_main);

        //init
        listFragment = new MainListFragment();
        defaultFragment = new MainDefaultFragment();

        //Settings
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int starts = preferences.getInt("starts", 0);

        starts++;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("starts", starts); // maybe for a rating dialog later
        editor.apply();

        Log.d(TAG, "onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        optionsMenu = menu;
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
            openDownloader();

        } else if (id == R.id.action_refreshCalendar) {
            //no sync if we dont have a calendar
            if (state.myCal == null)
                return true;

            state.SyncStart(this, true);
            Snackbar.make(findViewById(android.R.id.content), R.string.main_calendar_updating, Snackbar.LENGTH_SHORT).show();

        } else if (id == R.id.action_testNotfication) {
            if (state.myCal != null) {
                List<VEvent> evs = CalenderUtils.getNextEvents(state.myCal, 0);
                for (VEvent ev : evs) {
                    NotificationUtils.showEventNotification(ev, this);
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
        //not present --> intro text
        //present --> load info view

        try {
            state.InitCalender(this, true);

            if (state.myCal == null) {
                //show intro
                getFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, defaultFragment).commit();
            } else {
                getFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, listFragment).commit();
            }


        } catch (Exception ex) {
            Log.e(TAG, "Main onResume: ", ex);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        //GlobalState.Save(); //no changes yet
    }


    @Override
    public void openDownloader() {
        if (preferences.getBoolean("enableOldDL", false)) {
            Intent intent = new Intent(this, WebviewSelector.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, NativeSelector.class);
            startActivity(intent);
        }
    }
}

