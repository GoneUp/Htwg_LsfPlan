package com.hstrobel.lsfplan.gui;

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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.hstrobel.lsfplan.Globals;
import com.hstrobel.lsfplan.R;
import com.hstrobel.lsfplan.model.Utils;

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
        private boolean notifyChanged = false;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the settings from an XML resource
            addPreferencesFromResource(R.xml.settings);

            onSharedPreferenceChanged(Globals.settings, "");

            Preference myPref = findPreference("reset");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences.Editor editor = Globals.settings.edit();
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
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_github_url)));
                    startActivity(i);
                    return true;
                }
            });


            final ListPreference newpref = new ListPreference(getActivity());
            newpref.setTitle(R.string.pref_set_college);
            newpref.setSummary("EXPERIMENTAL");
            newpref.setEntries(new String[]{"HTWG", "UNI"});
            newpref.setEntryValues(new String[]{String.valueOf(Utils.MODE_HTWG), String.valueOf(Utils.MODE_UNI_KN)});
            newpref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (o != null) {
                        Globals.setCollege(Integer.parseInt((String) o));
                        Globals.cachedPlans = null;
                    }
                    return true;
                }
            });

            this.getPreferenceScreen().addPreference(newpref);

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case "notfiyTime":
                case "enableNotifications":
                    Globals.InitNotifications(getActivity());

                    notifyChanged = true;
                    break;
            }

            Preference myPref = findPreference("notfiyTime");
            int time = Integer.parseInt(Globals.settings.getString("notfiyTime", "15"));
            myPref.setSummary(String.format(getString(R.string.pref_description_timeSetter), time));

            ListPreference sPref = (ListPreference) findPreference("soundMode");
            if (sPref.getEntry() != null) sPref.setSummary(sPref.getEntry());

            DateFormat d = SimpleDateFormat.getDateTimeInstance();
            long time_load = Globals.settings.getLong("ICS_DATE", Integer.MAX_VALUE);
            GregorianCalendar syncTime = new GregorianCalendar();
            syncTime.setTimeInMillis(time_load);

            myPref = findPreference("enableRefresh");
            myPref.setSummary(String.format(getString(R.string.pref_description_refresh), d.format(syncTime.getTime())));


            myPref = findPreference("info");
            PackageInfo pInfo;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                myPref.setSummary(getString(R.string.pref_description_info) + pInfo.versionName);
            } catch (Exception e) {
            }

        }

        @Override
        public void onResume() {
            super.onResume();
            notifyChanged = false;

            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            super.onPause();

            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

            if (notifyChanged) {
                String info = String.valueOf(Globals.settings.getBoolean("enableNotifications", false)) + "_" + Globals.settings.getString("notfiyTime", "15");

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Globals.CONTENT_NOTIFY);
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, info);
                Globals.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        }
    }
}

