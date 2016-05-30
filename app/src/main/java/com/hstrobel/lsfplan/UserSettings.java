package com.hstrobel.lsfplan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;

import com.hstrobel.lsfplan.classes.Globals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class UserSettings extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @SuppressLint("ValidFragment")
    public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);

            onSharedPreferenceChanged(Globals.mSettings, "");

            Preference myPref = findPreference("reset");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences.Editor editor = Globals.mSettings.edit();
                    editor.clear();
                    editor.commit();
                    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.settings, true);
                    Globals.initialized = false;
                    NavUtils.navigateUpFromSameTask(getActivity());
                    return true;
                }
            });

            myPref = findPreference("github");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/GoneUp/Htwg_LsfPlan"));
                    startActivity(i);
                    return true;
                }
            });
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case "notfiyTime":
                case "enableNotifications":
                    Globals.InitNotifications(getActivity());
                    break;
            }

            Preference myPref = findPreference("notfiyTime");
            int time = Integer.parseInt(Globals.mSettings.getString("notfiyTime", "15"));
            myPref.setSummary(String.format(getString(R.string.pref_description_timeSetter), time));

            ListPreference sPref = (ListPreference) findPreference("soundMode");
            if (sPref.getEntry() != null) sPref.setSummary(sPref.getEntry());

            DateFormat d = SimpleDateFormat.getDateTimeInstance();
            long time_load = Globals.mSettings.getLong("ICS_DATE", Integer.MAX_VALUE);
            GregorianCalendar syncTime = new GregorianCalendar();
            syncTime.setTimeInMillis(time_load);

            myPref = findPreference("enableRefresh");
            myPref.setSummary(String.format(getString(R.string.pref_description_refresh), d.format(syncTime.getTime())));


            myPref = findPreference("info");
            PackageInfo pInfo = null;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                myPref.setSummary(getString(R.string.pref_description_info) + pInfo.versionName);
            } catch (Exception e) {
            }

        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }
    }
}

