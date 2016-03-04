package com.sciencesquad.health;

import android.app.AlarmManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.sciencesquad.health.events.BaseActivity;
import com.sciencesquad.health.events.BaseApplication;
import com.sciencesquad.health.prescriptions.PrescriptionAlarm;
import com.sciencesquad.health.prescriptions.PrescriptionModel;
import com.sciencesquad.health.ui.EmergencyNotification;

import java.util.Calendar;

public class ClockActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
