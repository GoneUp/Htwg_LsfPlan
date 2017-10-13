package com.hstrobel.lsfplan.gui;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
        View view = inflater.inflate(R.layout.fragment_main_def, container, false);

        Button downloadButton = (Button) view.findViewById(R.id.buttonIntro);
        downloadButton.setOnClickListener(v -> ((IOpenDownloader) getActivity()).openDownloader());

        return view;
    }


}
