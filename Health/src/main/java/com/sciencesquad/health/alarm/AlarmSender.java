package com.sciencesquad.health.alarm;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by andrew on 3/3/16.
 * Maintains a date and time and sends an alarm to AlarmManager
 */
public class AlarmSender {

	/** Variables for the various calendar fields
	 *
	 * HOUR_OF_DAY - Military time, 24-hour scheme
	 * HOUR - Standard 12 hour scheme
	 */
	public static final int YEAR = Calendar.YEAR;
	public static final int MONTH = Calendar.MONTH;
	public static final int DAY_OF_MONTH = Calendar.DAY_OF_MONTH;
	public static final int HOUR = Calendar.HOUR;
	public static final int HOUR_OF_DAY = Calendar.HOUR_OF_DAY;
	public static final int MINUTE = Calendar.MINUTE;
	/* Don't know why anyone would need these:
	static final int SECOND = Calendar.SECOND;
	static final int MILLISECOND = Calendar.MILLISECOND;
	*/

	private Calendar time;
	private boolean repeat = false;
	private long repeatInterval;
	private AlarmManager alarmMgr;

	private final int DEFAULT_REPEAT = 7;

	public AlarmSender() {
		this.time = Calendar.getInstance();
		this.time.set(Calendar.SECOND, 0);
		this.time.set(Calendar.MILLISECOND, 0);
		this.repeatInterval = AlarmManager.INTERVAL_DAY * DEFAULT_REPEAT;
	}

	/**
	 * Sends alarm using context and intent
	 * Intent is target activity
	 * Context is current activity
	 * @param context
	 * @param intent
	 */
	public void setAlarm(Context context, Intent intent) {
		Log.d("AlarmSender", "Alarm set");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (repeat) {
			alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, this.getTimeInMillis(), this.repeatInterval, pendingIntent);
		}
		else {
			alarmMgr.set(AlarmManager.RTC_WAKEUP, this.getTimeInMillis(), pendingIntent);
		}
	}

	public long getTimeInMillis() {
		return this.time.getTimeInMillis();
	}

	/** fields are the constants defined at the top (also can be found in the Java Calendar class) **/
	public int get(int field) {
		return this.time.get(field);
	}

	public void set(int field, int value) {
		this.time.set(field, value);
	}

	public void setTimeInMillis(long timeInMillis) {
		this.time.setTimeInMillis(timeInMillis);
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

	public long getRepeatInterval() {
		return this.repeatInterval;
	}

	public void setInterval(long interval) {
		this.repeatInterval = interval;
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
