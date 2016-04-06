package com.sciencesquad.health.run;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sciencesquad.health.R;

public class RunLandingFragment extends Fragment{
    public static final String TAG = RunLandingFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceSate) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_run_landing, container, false);
        //Button
        final Button button1 = (Button) view.findViewById(R.id.buttonFreeRun);
        final Button button2 = (Button) view.findViewById(R.id.buttonBuildRoute);
        final Button button3 = (Button) view.findViewById(R.id.buttonTrainingProgram);
        //Button Click
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button1Clicked(v);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button2Clicked(v);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button3Clicked(v);
            }
        });
        return view;
    }

    public void button1Clicked(View view) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new RunFragment(), RunFragment.TAG)
                .addToBackStack(RunFragment.TAG)
                .commit();
    }
    public void button2Clicked(View view) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new CreateRouteFragment(), CreateRouteFragment.TAG)
                .addToBackStack(CreateRouteFragment.TAG)
                .commit();
    }
    public void button3Clicked(View view) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new RunTrainingFragment(), RunTrainingFragment.TAG)
                .addToBackStack(RunTrainingFragment.TAG)
                .commit();
    }

}