package com.sciencesquad.health.run;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sciencesquad.health.R;


public class RunTrainingFragment extends Fragment {
    public static final String TAG = RunTrainingFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_run_training, container, false);
    }

}
