package com.sciencesquad.health.prescriptions;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.alarm.AlarmModule;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

/**
 * Created by andrew on 3/3/16.
 * Initiates the Material Design Picker dialog
 * Sets the AlarmSend variable in this class
 */
public class AlarmDialog implements TimePickerDialog.OnTimeSetListener,
		DatePickerDialog.OnDateSetListener {

	AlarmModule alarmModule;

	int alarmId;
	Runnable onFinish;
	Activity parentActivity;

	class mListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
	}

	public AlarmDialog() {
		alarmModule = Module.of(AlarmModule.class);
	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
		alarmModule.setAlarmById(this.alarmId);
		alarmModule.set(Calendar.HOUR_OF_DAY, hourOfDay)
				.set(Calendar.MINUTE, minute);
		alarmModule.add();
		this.onFinish.run();
	}

	@Override
	public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
		alarmModule.setAlarmById(this.alarmId);
		alarmModule.set(Calendar.YEAR, year)
				.set(Calendar.MONTH, monthOfYear)
				.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		alarmModule.add();
		this.callTimePicker(this.alarmId, this.onFinish, this.parentActivity, false);
	}

	public void callDatePicker(int alarmId, Runnable onFinish, Activity parentActivity) {
		this.alarmId = alarmId;
		this.onFinish = onFinish;
		this.parentActivity = parentActivity;
		alarmModule.setAlarmById(this.alarmId);
		DatePickerDialog dpd = DatePickerDialog.newInstance(this,
				alarmModule.get(Calendar.YEAR),
				alarmModule.get(Calendar.MONTH),
				alarmModule.get(Calendar.DAY_OF_MONTH));
		dpd.show(parentActivity.getFragmentManager(), "Datepickerdialog");
	}

	public void callTimePicker(int alarmId, Runnable onFinish, Activity parentActivity, boolean is24HourMode) {
		this.alarmId = alarmId;
		this.onFinish = onFinish;
		this.parentActivity = parentActivity;
		alarmModule.setAlarmById(this.alarmId);
		TimePickerDialog tpd = TimePickerDialog.newInstance(this,
				alarmModule.get(Calendar.HOUR_OF_DAY),
				alarmModule.get(Calendar.MINUTE),
				is24HourMode);
		tpd.show(parentActivity.getFragmentManager(), "Timepickerdialog");
	}
}
