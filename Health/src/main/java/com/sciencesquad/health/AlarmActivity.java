package com.sciencesquad.health;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.sciencesquad.health.alarm.AlarmDialog;
import com.sciencesquad.health.alarm.AlarmSender;
import com.sciencesquad.health.events.BaseApplication;
import com.sciencesquad.health.prescriptions.PrescriptionAlarm;
import com.sciencesquad.health.prescriptions.PrescriptionAlarmReceiver;
import com.sciencesquad.health.prescriptions.PrescriptionModel;
import com.sciencesquad.health.steps.StepsViewModel;
import com.sciencesquad.health.workout.WorkoutActivity;
import com.sciencesquad.health.activity.MapsActivity;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity implements
		NavigationView.OnNavigationItemSelectedListener {

	private boolean alarmSet = false;
	AlarmDialog dialog;

	private Runnable createRunnable(AlarmDialog dialog, Context context, Intent intent) {
		return () -> dialog.setAlarm(context, intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/* PrescriptionAlarmReceiver receiver = new PrescriptionAlarmReceiver();
				receiver.doSomethingImportant("Tylenol", 5); */
				if (dialog == null) {
					dialog = new AlarmDialog(view.getContext());
				}
				if (!dialog.isSet()) {
					dialog.callDatePicker();
				}
				else {
					AlarmSender alarm = dialog.getAlarm();

					alarm.setInterval(AlarmManager.INTERVAL_DAY);

					PrescriptionModel prescriptionModel = new PrescriptionModel();

					prescriptionModel.setDosage(5);
					prescriptionModel.setName("Tylenol");
					prescriptionModel.setRepeatDuration(alarm.getRepeatInterval());
					prescriptionModel.setStartDate(alarm.getTimeInMillis());

					PrescriptionAlarm.setAlarm(prescriptionModel, view.getContext());
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
