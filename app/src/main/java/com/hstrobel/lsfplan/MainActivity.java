package com.hstrobel.lsfplan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.hstrobel.lsfplan.frags.MainDefaultFragment;
import com.hstrobel.lsfplan.frags.MainListFragment;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Collection;
import java.util.List;

public class MainActivity extends ActionBarActivity {

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

        System.out.println("start");
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
            Globals.InitCalender(this);


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
                    transaction.commit();
                }
/*
                Collection<VEvent> evs = CalenderUtils.GetNextEvent(Globals.myCal);
                StringBuilder builder = new StringBuilder();
                for (VEvent ev : evs) {
                    builder.append(ev.toString() + "\n" + CalenderUtils.formatEventLong(ev, this));
                }
                infoText.setText(builder.toString());
                */
            }


        } catch (Exception ex) {
            System.out.println("FAIL DL:\n " + ExceptionUtils.getCause(ex));
            System.out.println("FAIL DL ST:\n " + ExceptionUtils.getFullStackTrace(ex));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("ondestory");
        //Globals.Save();
    }
}

