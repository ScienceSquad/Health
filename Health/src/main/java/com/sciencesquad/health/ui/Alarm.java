package com.sciencesquad.health.ui;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by andrew on 3/3/16.
 */
public class Alarm {

	/** Variables for the various calendar fields
	 *
	 * HOUR_OF_DAY - Military time, 24-hour scheme
	 * HOUR - Standard 12 hour scheme
	 */
	static final int YEAR = Calendar.YEAR;
	static final int MONTH = Calendar.MONTH;
	static final int DAY_OF_MONTH = Calendar.DAY_OF_MONTH;
	static final int HOUR = Calendar.HOUR;
	static final int HOUR_OF_DAY = Calendar.HOUR_OF_DAY;
	static final int MINUTE = Calendar.MINUTE;
	/* Don't know why anyone would need these:
	static final int SECOND = Calendar.SECOND;
	static final int MILLISECOND = Calendar.MILLISECOND;
	*/

	private Calendar time;
	private boolean repeat = false;
	private long repeatInterval;
	private AlarmManager alarmMgr;
	private PendingIntent pendingIntent;

	private final int DEFAULT_REPEAT = 7;

	public Alarm() {
		this.time = Calendar.getInstance();
		this.time.set(Calendar.SECOND, 0);
		this.time.set(Calendar.MILLISECOND, 0);
		this.repeatInterval = AlarmManager.INTERVAL_DAY * DEFAULT_REPEAT;
	}

	public void setAlarm(Context context) {
		this.pendingIntent = this.getPendingIntent(context);
		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (repeat) {
			alarmMgr.setInexactRepeating(AlarmManager.RTC, this.getTimeInMillis(), this.repeatInterval, this.pendingIntent);
		}
		else {
			alarmMgr.set(AlarmManager.RTC, this.getTimeInMillis(), this.pendingIntent);
		}
	}

	private PendingIntent getPendingIntent(Context context) {
		Intent intent = new Intent(context, AlarmReceiver.class);
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

	public void setIntervalDays(int days) {
		if (days <= 0) return;
		this.repeatInterval = AlarmManager.INTERVAL_DAY * days;
	}

	public void setIntervalHours(int hours) {
		if (hours <= 0) return;
		this.repeatInterval = AlarmManager.INTERVAL_HOUR * hours;
	}

	public void setIntervalMinutes(int minutes) {
		int intervals = minutes / 15;
		if (intervals <= 0) return;
		this.repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES * intervals;
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
