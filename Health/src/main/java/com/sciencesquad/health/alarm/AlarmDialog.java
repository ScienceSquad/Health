package com.sciencesquad.health.alarm;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

/**
 * Created by andrew on 3/3/16.
 */
public class AlarmDialog implements TimePickerDialog.OnTimeSetListener,
		DatePickerDialog.OnDateSetListener {

	private AlarmSender alarm;
	private Activity parentActivity;
	private Runnable onDateTimeSet;

	class mListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
	}

	public AlarmDialog(Context context) {
		this.parentActivity = (Activity) context;
		this.alarm = new AlarmSender();
	}

	public void setOnDateTimeSet(Runnable onDateTimeSet) {
		this.onDateTimeSet = onDateTimeSet;
	}

	public void setAlarm(Context context, Intent intent) {
		this.alarm.setAlarm(context, intent);
	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
		this.alarm.set(AlarmSender.HOUR_OF_DAY, hourOfDay);
		this.alarm.set(AlarmSender.MINUTE, minute);
		if (onDateTimeSet != null) {
			onDateTimeSet.run();
		}
	}

	@Override
	public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
		this.alarm.set(AlarmSender.YEAR, year);
		this.alarm.set(AlarmSender.MONTH, monthOfYear);
		this.alarm.set(AlarmSender.DAY_OF_MONTH, dayOfMonth);
		this.callTimePicker(false);
	}

	public void callDatePicker() {
		DatePickerDialog dpd = DatePickerDialog.newInstance(this,
				this.alarm.get(AlarmSender.YEAR),
				this.alarm.get(AlarmSender.MONTH),
				this.alarm.get(AlarmSender.DAY_OF_MONTH));
		dpd.show(this.parentActivity.getFragmentManager(), "Datepickerdialog");
	}

	public void callTimePicker(boolean is24HourMode) {
		TimePickerDialog tpd = TimePickerDialog.newInstance(this,
				this.alarm.get(AlarmSender.HOUR_OF_DAY),
				this.alarm.get(AlarmSender.MINUTE),
				is24HourMode);
		tpd.show(this.parentActivity.getFragmentManager(), "Timepickerdialog");
	}
}
