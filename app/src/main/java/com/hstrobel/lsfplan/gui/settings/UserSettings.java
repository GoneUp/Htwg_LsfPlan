package com.hstrobel.lsfplan.gui.settings;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.hstrobel.lsfplan.GlobalState;

public class UserSettings extends AppCompatActivity {
    private GlobalState state = GlobalState.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

}

