package com.sciencesquad.health;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sciencesquad.health.alarm.AlarmDialog;
import com.sciencesquad.health.alarm.AlarmSender;
import com.sciencesquad.health.events.BaseApplication;
import com.sciencesquad.health.prescriptions.PrescriptionAlarm;
import com.sciencesquad.health.prescriptions.PrescriptionAlarmReceiver;
import com.sciencesquad.health.prescriptions.PrescriptionModel;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

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

}
