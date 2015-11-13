package com.hstrobel.lsfplan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;

import com.hstrobel.lsfplan.classes.Globals;

public class UserSettings extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);

            onSharedPreferenceChanged(Globals.mSettings, "");

            Preference myPref = findPreference("reset");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    SharedPreferences.Editor editor = Globals.mSettings.edit();
                    editor.clear();
                    editor.commit();
                    Globals.initalized = false;
                    NavUtils.navigateUpFromSameTask(getActivity());
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
            sPref.setSummary(sPref.getEntry());
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

