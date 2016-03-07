package com.sciencesquad.health;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.sciencesquad.health.events.BaseActivity;
import com.sciencesquad.health.events.BaseApplication;
import com.sciencesquad.health.prescriptions.PrescriptionAlarm;
import com.sciencesquad.health.prescriptions.PrescriptionModel;
import com.sciencesquad.health.steps.StepsViewModel;
import com.sciencesquad.health.ui.EmergencyNotification;
import com.sciencesquad.health.workout.WorkoutActivity;
import com.sciencesquad.health.activity.MapsActivity;

import java.util.Calendar;

public class ClockActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {

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

    /**
     * I have not been tested :-)
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_run) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_sleep) {

        } else if (id == R.id.nav_steps) {
            Intent intent = new Intent(this, StepsViewModel.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_workout){
            Intent intent = new Intent(this, WorkoutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_clock) {
            Intent intent = new Intent(this, ClockActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_alarm) {
            Intent intent = new Intent(this, AlarmActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
