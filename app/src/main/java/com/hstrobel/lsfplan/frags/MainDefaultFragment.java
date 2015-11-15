package com.hstrobel.lsfplan.frags;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hstrobel.lsfplan.R;

public class MainDefaultFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LSF", "MainDefaultFragment:onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("LSF", "MainDefaultFragment:onCreateView");
        return inflater.inflate(R.layout.fragment_main_def, container, false);
    }


}
