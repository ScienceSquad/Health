package com.sciencesquad.health.core;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import android.app.Fragment;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.ui.EmergencyNotification;

public class ClockFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EmergencyNotification.hasSent()) {
                    EmergencyNotification.closeNotification(view.getContext());
                }
                else {
                    EmergencyNotification.sendNotification(view.getContext(), "THIS IS AN EMERGENCY",
                            "This is actually a pretty big deal, you should be concerned.");
                }
            }
        });
    }
}
