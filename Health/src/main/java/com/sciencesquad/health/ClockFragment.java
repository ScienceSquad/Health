package com.sciencesquad.health;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.sciencesquad.health.alarm.AlarmFragment;
import com.sciencesquad.health.steps.StepsFragment;
import com.sciencesquad.health.ui.EmergencyNotification;
import com.sciencesquad.health.workout.WorkoutFragment;
import com.sciencesquad.health.activity.MapsActivity;

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
