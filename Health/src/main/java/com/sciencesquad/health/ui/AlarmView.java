package com.sciencesquad.health.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.sciencesquad.health.R;
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
				this.callDatePicker();
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
	}

	@Override
	public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
		this.alarm.set(Calendar.YEAR, year);
		this.alarm.set(Calendar.MONTH, monthOfYear);
		this.alarm.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		this.postInvalidate();
		this.callTimePicker(false, false);
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawText(this.alarm.getFieldString(Calendar.YEAR, true), 0, this.mTextHeight, mTextPaint);
		canvas.drawText(this.alarm.getFieldString(Calendar.MONTH, true), 0, 2 * this.mTextHeight, mTextPaint);
		canvas.drawText(this.alarm.getFieldString(Calendar.DAY_OF_MONTH, true), 0, 3 * this.mTextHeight, mTextPaint);
		canvas.drawText(this.alarm.getFieldString(Calendar.HOUR_OF_DAY, true), 0, 4 * this.mTextHeight, mTextPaint);
		canvas.drawText(this.alarm.getFieldString(Calendar.MINUTE, true), 0, 5 * this.mTextHeight, mTextPaint);
		canvas.drawText(this.alarm.getFieldString(Calendar.SECOND, true), 0, 6 * this.mTextHeight, mTextPaint);
	}

	public void callDatePicker() {
		DatePickerDialog dpd = DatePickerDialog.newInstance(this,
				this.alarm.get(Calendar.YEAR),
				this.alarm.get(Calendar.MONTH),
				this.alarm.get(Calendar.DAY_OF_MONTH));
		dpd.show(this.parentActivity.getFragmentManager(), "Datepickerdialog");
	}

	public void callTimePicker(boolean includeSecond, boolean is24HourMode) {
		TimePickerDialog tpd = TimePickerDialog.newInstance(this,
				this.alarm.get(Calendar.HOUR_OF_DAY),
				this.alarm.get(Calendar.MINUTE),
				this.alarm.get(Calendar.SECOND),
				is24HourMode);
		tpd.show(this.parentActivity.getFragmentManager(), "Timepickerdialog");
	}
}
