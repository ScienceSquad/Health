package com.sciencesquad.health.alarm;

import android.app.AlarmManager;
import android.content.Context;
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

import com.sciencesquad.health.ClockFragment;
import com.sciencesquad.health.R;
import com.sciencesquad.health.prescriptions.PrescriptionAlarm;
import com.sciencesquad.health.prescriptions.PrescriptionModel;
import com.sciencesquad.health.steps.StepsFragment;
import com.sciencesquad.health.workout.WorkoutFragment;
import com.sciencesquad.health.activity.MapsActivity;

public class AlarmFragment extends Fragment {

	private boolean alarmSet = false;
	AlarmDialog dialog;

	private Runnable createRunnable(AlarmDialog dialog, Context context, Intent intent) {
		return () -> dialog.setAlarm(context, intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
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

}
