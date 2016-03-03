package com.sciencesquad.health.ui;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sciencesquad.health.AlarmActivity;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.temporal.TemporalUnit;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by andrew on 3/3/16.
 */
public class Alarm {

	private Calendar time;
	private boolean repeat = false;
	private Duration repeatDuration;
	private AlarmManager alarmMgr;
	private PendingIntent pendingIntent;

	private final int DEFAULT_REPEAT = 7;

	public Alarm() {
		this.time = Calendar.getInstance();
		this.time.set(Calendar.SECOND, 0);
		this.time.set(Calendar.MILLISECOND, 0);
		this.repeatDuration = Duration.ZERO.plusDays(DEFAULT_REPEAT);
	}

	public void setAlarm(Context context) {
		this.pendingIntent = this.getPendingIntent(context);
		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.set(AlarmManager.RTC, this.getTimeInMillis(), this.pendingIntent);
	}

	private PendingIntent getPendingIntent(Context context) {
		Intent intent = new Intent(context, AlarmActivity.class);
		// TODO add putExtra functions to know what alarm this is
		return PendingIntent.getActivity(context, 0, intent, 0);
	}

	public long getTimeInMillis() {
		return this.time.getTimeInMillis();
	}

	public int get(int field) {
		return this.time.get(field);
	}

	public void set(int field, int value) {
		this.time.set(field, value);
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public void toggleRepeat() {
		this.repeat = !this.repeat;
	}

	public void setRepeatDuration(Duration repeatDuration) {
		this.repeatDuration = repeatDuration;
	}

	public void setRepeatDuration(int days) {
		this.repeatDuration = Duration.ZERO.plusDays(days);
	}

	public String getFieldString(int field, boolean prettyName) {
		String value = String.valueOf(this.get(field));
		if (prettyName) {
			if (field == Calendar.MONTH) {
				value = this.time.getDisplayName(field, Calendar.LONG, Locale.US);
			}
		}
		return value;
	}

}
