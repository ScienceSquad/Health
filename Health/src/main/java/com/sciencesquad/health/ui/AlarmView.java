package com.sciencesquad.health.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

/**
 * Created by andrew on 3/3/16.
 */
public class AlarmView extends View implements TimePickerDialog.OnTimeSetListener,
		DatePickerDialog.OnDateSetListener {

	private Alarm alarm;

	private GestureDetector mDetector;

	private Paint mTextPaint;

	private Activity parentActivity;

	private boolean timeSet = false;

	private int mTextColor = Color.BLACK;
	private float mTextHeight = 50;

	class mListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
	}

	public AlarmView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		mDetector = new GestureDetector(this.getContext(), new mListener());
		this.alarm = new Alarm();

		this.parentActivity = (Activity) context;

		this.setPaints();
	}

	private void setPaints() {
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(mTextColor);
		mTextPaint.setTextSize(mTextHeight);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean result = mDetector.onTouchEvent(event);
		if (!result) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (this.timeSet) {
					this.alarm.setAlarm(this.parentActivity.getApplicationContext());
				}
				else {
					this.callDatePicker();
				}
				result = true;
			}
		}
		return result;
	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
		this.alarm.set(Calendar.HOUR_OF_DAY, hourOfDay);
		this.alarm.set(Calendar.MINUTE, minute);
		this.alarm.set(Calendar.SECOND, second);
		this.postInvalidate();
		this.timeSet = true;
	}

	@Override
	public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
		this.alarm.set(Calendar.YEAR, year);
		this.alarm.set(Calendar.MONTH, monthOfYear);
		this.alarm.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		this.postInvalidate();
		this.callTimePicker(false);
	}

	@Override
	public void onDraw(Canvas canvas) {
		String year = this.alarm.getFieldString(Calendar.YEAR, true);
		String month = this.alarm.getFieldString(Calendar.MONTH, true);
		String day = this.alarm.getFieldString(Calendar.DAY_OF_MONTH, true);
		String hour = this.alarm.getFieldString(Calendar.HOUR_OF_DAY, true);
		String minute = this.alarm.getFieldString(Calendar.MINUTE, true);
		String second = this.alarm.getFieldString(Calendar.SECOND, true);
		String date = month + " " + day + ", " + year + " @ " + hour + ":" + minute + ":" + second;
		canvas.drawText(date, 0, this.mTextHeight, mTextPaint);
	}

	public void callDatePicker() {
		DatePickerDialog dpd = DatePickerDialog.newInstance(this,
				this.alarm.get(Calendar.YEAR),
				this.alarm.get(Calendar.MONTH),
				this.alarm.get(Calendar.DAY_OF_MONTH));
		dpd.show(this.parentActivity.getFragmentManager(), "Datepickerdialog");
	}

	public void callTimePicker(boolean is24HourMode) {
		TimePickerDialog tpd = TimePickerDialog.newInstance(this,
				this.alarm.get(Calendar.HOUR_OF_DAY),
				this.alarm.get(Calendar.MINUTE),
				this.alarm.get(Calendar.SECOND),
				is24HourMode);
		tpd.show(this.parentActivity.getFragmentManager(), "Timepickerdialog");
	}
}
