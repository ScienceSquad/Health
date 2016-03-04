package com.sciencesquad.health;

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
				Context context = view.getContext();
				Intent intent = new Intent(context, AlarmActivity.class);
				dialog = new AlarmDialog(context);
				dialog.setOnDateTimeSet(createRunnable(dialog, context, intent));
				dialog.callDatePicker();
			}
		});
	}

}
