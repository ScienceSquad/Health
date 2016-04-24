package com.sciencesquad.health.core.alarm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.EventBus;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import io.realm.RealmResults;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by andrew on 4/13/16.
 */
public class AlarmModule extends Module {
	static final String TAG = AlarmModule.class.getSimpleName();
	static final String ALARM_ID_FIELD = "alarmId";

	private RealmContext<AlarmModel> alarmRealm;

	@Override
	public void onStart() {
		this.alarmRealm = new RealmContext<>();
		this.alarmRealm.init(BaseApp.app(), AlarmModel.class, "alarm.realm");

		resetData();
		bus().subscribe("AlarmFiredEvent", null, ev -> {
			int alarmId = (Integer) ev.get("alarmId");
			sendAlarm(getAlarmById(alarmId), true);
		});
	}

	@Override
	public void onStop() {

	}

	public enum RepeatInterval {
		ONCE,
		DAILY,
		DAY_SPECIFIC
	}

	private Calendar time;
	private RepeatInterval repeatInterval;
	private int alarmId;
	private ArrayList<Integer> daysOfWeek;
	private boolean active;
	private int numDays;

	private final int DEFAULT_REPEAT = 1;

	private void resetData() {
		this.time = Calendar.getInstance();
		this.time.set(Calendar.SECOND, 0);
		this.time.set(Calendar.MILLISECOND, 0);
		this.repeatInterval = RepeatInterval.ONCE;
		this.alarmId = -1;
		this.daysOfWeek = new ArrayList<>();
		this.active = true;
		this.numDays = 1;
	}


	private int arrayListToInt(ArrayList<Integer> arr) {
		int n = 0;
		for (int i = 1; i <= 7; i++)
			n = (n << 1) | (arr.contains(i) ? 1 : 0);
		return n;
	}

	private ArrayList<Integer> intToArrayList(int n) {
		ArrayList<Integer> arr = new ArrayList<>();
		for (int i = 7; i >= 1; i--) {
			if ((n & 1) != 0) arr.add(i);
			n = n >> 1;
		}
		return arr;
	}

	private int getNextDayOfWeek(AlarmModel alarm, int currentDay) {
		ArrayList<Integer> daysOfWeek = intToArrayList(alarm.getDaysOfWeek());
		if (daysOfWeek.size() == 0) return -1;
		Collections.sort(daysOfWeek);
		for (int day : daysOfWeek) {
			if (day >= currentDay) {
				return day;
			}
		}
		return daysOfWeek.get(0);
	}

	private void getNextAlarmTime(AlarmModel alarm, boolean hasFired) {
		/* 	If ONCE
		 		If hasFired
		 			toggle alarm off
				else
					get next time, today or tomorrow
			If DAILY
				get next time
			If DAY_SPECIFIC
				get next specific day and time on that day
		*/

		Calendar now = Calendar.getInstance();
		Calendar next = Calendar.getInstance();
		Calendar alarmTime = Calendar.getInstance();
		alarmTime.setTimeInMillis(alarm.getTime());
		next.set(Calendar.HOUR_OF_DAY, alarmTime.get(Calendar.HOUR_OF_DAY));
		next.set(Calendar.MINUTE, alarmTime.get(Calendar.MINUTE));
		next.set(Calendar.SECOND, alarmTime.get(Calendar.SECOND));
		next.set(Calendar.MILLISECOND, alarmTime.get(Calendar.MILLISECOND));
		RepeatInterval repeatInterval = intToRepeatInterval(alarm.getRepeatInterval());
		switch (repeatInterval) {
			case DAY_SPECIFIC:
				int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
				if (next.getTimeInMillis() <= now.getTimeInMillis()) dayOfWeek++;
				dayOfWeek = getNextDayOfWeek(alarm, dayOfWeek);
				if (dayOfWeek > -1) {
					next.set(Calendar.DAY_OF_WEEK, dayOfWeek);
					if (next.getTimeInMillis() <= now.getTimeInMillis()) {
						next.add(Calendar.WEEK_OF_MONTH, 1);
					}
					break;
				}
			case ONCE:
			default:
				if (hasFired) {
					this.setActive(alarm, false);
					break;
				}
			case DAILY:
				if (alarm.getNumDays() > 0) {
					if (next.getTimeInMillis() <= now.getTimeInMillis()) {
						next.add(Calendar.DAY_OF_MONTH, alarm.getNumDays());
					}
				}
				else {
					this.setActive(alarm, false);
					break;
				}
				break;
		}
		alarmRealm.getRealm().beginTransaction();
		alarm.setTime(next.getTimeInMillis());
		alarmRealm.getRealm().commitTransaction();
	}

	/** Get a unique alarm ID
	 *
	 * @return
	 */
	private int getNextAlarmId() {

		int alarmId;

		/** Get a random positive nonzero ID and check if an alarm with that ID already exists **/
		do {
			alarmId = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
		} while (this.alarmRealm.query(AlarmModel.class)
				.equalTo(ALARM_ID_FIELD, alarmId).findAll()
				.size() > 0);

		Log.d(TAG, "Alarm ID: " + String.valueOf(alarmId));

		return alarmId;
	}

	/**
	 * Functions for managing the time of the alarm
	 */
	public long getTimeInMillis() {
		return this.time.getTimeInMillis();
	}

	/** fields are the constants defined at the top (also can be found in the Java Calendar class) **/
	public int get(int field) {
		return this.time.get(field);
	}

	public AlarmModule set(int field, int value) {
		this.time.set(field, value);
		return this;
	}

	public AlarmModule setTimeInMillis(long timeInMillis) {
		this.time.setTimeInMillis(timeInMillis);
		return this;
	}

	public void setActive(boolean active) { this.active = active; }
	public boolean isActive() { return this.active; }

	public RepeatInterval getRepeatInterval() {
		return this.repeatInterval;
	}

	public AlarmModule setRepeatInterval(RepeatInterval interval) {
		this.repeatInterval = interval;
		return this;
	}

	public AlarmModule addDayOfWeek(int day) {
		if (!this.daysOfWeek.contains(day)) this.daysOfWeek.add(day);
		return this;
	}

	public AlarmModule removeDayOfWeek(int day) {
		if (this.daysOfWeek.contains(day)) this.daysOfWeek.remove(this.daysOfWeek.indexOf(day));
		return this;
	}

	public boolean isOnDayOfWeek(int day) {
		return this.daysOfWeek.contains(day);
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

	public String getPrettyTime() {
		String minute = getFieldString(Calendar.MINUTE, false);
		int hour = get(Calendar.HOUR);

		if (hour == 0) hour = 12;

		if (minute.length() < 2) minute = "0" + minute;

		return String.valueOf(hour) + ":" + minute;
	}

	public String getTimePeriod() {
		int hourOfDay = get(Calendar.HOUR_OF_DAY);
		if (hourOfDay > 11) return "PM";
		return "AM";
	}

	public void sendAlarm(AlarmModel alarm, boolean hasFired) {

		getNextAlarmTime(alarm, hasFired);

		if (!alarm.getActive()) return;

		PendingIntent pendingIntent = getAlarmIntent(alarm);

		AlarmManager alarmMgr = (AlarmManager) BaseApp.app()
				.getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);

		this.copyAlarm(alarm);

		alarmMgr.set(AlarmManager.RTC_WAKEUP, this.getTimeInMillis(), pendingIntent);
	}

	public void sendAlarm(AlarmModel alarm) {
		sendAlarm(alarm, false);
	}

	public void sendAll() {
		RealmResults<AlarmModel> results = alarmRealm.query(AlarmModel.class).findAll();
		for (AlarmModel alarm : results) {
			sendAlarm(alarm);
		}
	}

	public void cancelAlarm(AlarmModel alarm) {
		PendingIntent pendingIntent = getAlarmIntent(alarm);

		AlarmManager alarmMgr = (AlarmManager) BaseApp.app()
				.getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);

		alarmMgr.cancel(pendingIntent);
	}

	public void removeAlarm(AlarmModel alarm) {
		if (alarm == null) return;
		cancelAlarm(alarm);

		alarmRealm.getRealm().beginTransaction();
		alarm.removeFromRealm();
		alarmRealm.getRealm().commitTransaction();
	}

	public void removeAlarmById(int alarmId) {
		removeAlarm(getAlarmById(alarmId));
	}

	private int repeatIntervalToInt(RepeatInterval repeatInterval) {
		switch (repeatInterval) {
			case DAILY: return 1;
			case DAY_SPECIFIC: return 2;
			default: return 0;
		}
	}

	private RepeatInterval intToRepeatInterval(int repeatInterval) {
		switch (repeatInterval) {
			case 1: return RepeatInterval.DAILY;
			case 2: return RepeatInterval.DAY_SPECIFIC;
			default: return RepeatInterval.ONCE;
		}
	}

	/**
	 * Adds a new AlarmModel with the set data
	 * and returns the alarm so you can do magic with it
	 */
	public AlarmModel add() {
		AlarmModel alarm = getAlarmById(this.alarmId);

		if (alarm == null) {
			alarm = new AlarmModel();
			alarm.setAlarmId(getNextAlarmId());
		}
		else {
			cancelAlarm(alarm);
			this.alarmRealm.getRealm().beginTransaction();
		}

		alarm.setRepeatInterval(repeatIntervalToInt(repeatInterval));
		alarm.setDaysOfWeek(arrayListToInt(daysOfWeek));
		alarm.setTime(time.getTimeInMillis());
		alarm.setActive(this.active);
		alarm.setNumDays(this.numDays);

		if (this.alarmId > -1) {
			this.alarmRealm.getRealm().commitTransaction();
		}
		else
			this.alarmRealm.add(alarm);

		sendAlarm(alarm);

		this.resetData();

		return alarm;
	}

	/**
	 * Get the alarm with the specified ID
	 * Will be used in the BroadcastReceiver to figure out what to do when the alarm fires
	 * @param alarmId
	 * @return
	 */
	public AlarmModel getAlarmById(int alarmId) {
		RealmResults<AlarmModel> results = this.alarmRealm.query(AlarmModel.class)
				.equalTo(ALARM_ID_FIELD, alarmId)
				.findAll();

		if (results.size() > 0) return results.get(0);

		return null;
	}

	public AlarmModule setActive(AlarmModel alarm, boolean active) {
		if (alarm == null) return this;
		cancelAlarm(alarm);
		alarmRealm.getRealm().beginTransaction();
		alarm.setActive(active);
		alarmRealm.getRealm().commitTransaction();
		sendAlarm(alarm);
		return this;
	}

	public AlarmModule setActive(int alarmId, boolean active) {
		return setActive(getAlarmById(alarmId), active);
	}

	public AlarmModule setTimeInMillis(AlarmModel alarm, long timeInMillis) {
		if (alarm == null) return this;
		cancelAlarm(alarm);
		alarmRealm.getRealm().beginTransaction();
		alarm.setTime(timeInMillis);
		alarmRealm.getRealm().commitTransaction();
		sendAlarm(alarm);
		return this;
	}

	public AlarmModule setTimeInMillis(int alarmId, long timeInMillis) {
		return setTimeInMillis(getAlarmById(alarmId), timeInMillis);
	}

	public AlarmModule setRepeatInterval(AlarmModel alarm, int repeatInterval) {
		if (alarm == null) return this;
		cancelAlarm(alarm);
		alarmRealm.getRealm().beginTransaction();
		alarm.setRepeatInterval(repeatInterval);
		alarmRealm.getRealm().commitTransaction();
		sendAlarm(alarm);
		return this;
	}

	public AlarmModule setRepeatInterval(int alarmId, int repeatInterval) {
		return setRepeatInterval(getAlarmById(alarmId), repeatInterval);
	}

	public AlarmModule setRepeatInterval(AlarmModel alarm, RepeatInterval repeatInterval) {
		return setRepeatInterval(alarm, repeatIntervalToInt(repeatInterval));
	}

	public AlarmModule setRepeatInterval(int alarmId, RepeatInterval repeatInterval) {
		return setRepeatInterval(getAlarmById(alarmId), repeatInterval);
	}

	public AlarmModule setDaysOfWeek(AlarmModel alarm, ArrayList<Integer> daysOfWeek) {
		if (alarm == null) return this;
		cancelAlarm(alarm);
		alarmRealm.getRealm().beginTransaction();
		alarm.setRepeatInterval(arrayListToInt(daysOfWeek));
		alarmRealm.getRealm().commitTransaction();
		sendAlarm(alarm);
		return this;
	}

	public AlarmModule setDaysOfWeek(int alarmId, ArrayList<Integer> daysOfWeek) {
		return setDaysOfWeek(getAlarmById(alarmId), daysOfWeek);
	}

	public ArrayList<Integer> getDaysOfWeek() { return this.daysOfWeek; }

	public AlarmModule setNumDays(AlarmModel alarm, int numDays) {
		if (alarm == null) return this;
		cancelAlarm(alarm);
		alarmRealm.getRealm().beginTransaction();
		alarm.setNumDays(numDays);
		alarmRealm.getRealm().commitTransaction();
		sendAlarm(alarm);
		return this;
	}

	public AlarmModule setNumDays(int alarmId, int numDays) {
		return setDaysOfWeek(getAlarmById(alarmId), daysOfWeek);
	}

	public AlarmModule setNumDays(int numDays) {
		this.numDays = numDays;
		return this;
	}

	public AlarmModule addDayOfWeek(AlarmModel alarm, int day) {
		if (alarm == null) return this;
		ArrayList<Integer> daysOfWeek = intToArrayList(alarm.getDaysOfWeek());
		if (!daysOfWeek.contains(day)) {
			daysOfWeek.add(day);
		}
		setDaysOfWeek(alarm, daysOfWeek);
		return this;
	}

	public AlarmModule addDayOfWeek(int alarmId, int day) {
		return addDayOfWeek(getAlarmById(alarmId), day);
	}

	public AlarmModule removeDayOfWeek(AlarmModel alarm, int day) {
		if (alarm == null) return this;
		ArrayList<Integer> daysOfWeek = intToArrayList(alarm.getDaysOfWeek());
		if (daysOfWeek.contains(day)) {
			daysOfWeek.remove(daysOfWeek.indexOf(day));
		}
		setDaysOfWeek(alarm, daysOfWeek);
		return this;
	}

	public AlarmModule removeDayOfWeek(int alarmId, int day) {
		return addDayOfWeek(getAlarmById(alarmId), day);
	}

	public void copyAlarm(AlarmModel alarm) {
		this.resetData();
		this.time.setTimeInMillis(alarm.getTime());
		this.repeatInterval = intToRepeatInterval(alarm.getRepeatInterval());
		this.alarmId = alarm.getAlarmId();
		this.daysOfWeek = intToArrayList(alarm.getDaysOfWeek());
		this.active = alarm.getActive();
		this.numDays = alarm.getNumDays();
	}

	public AlarmModule setAlarmById(int alarmId) {
		AlarmModel alarm = getAlarmById(alarmId);
		copyAlarm(alarm);
		return this;
	}

	public void handleAlarm(int alarmId) {

		AlarmModel alarm = getAlarmById(alarmId);

		String repeatType;

		switch (intToRepeatInterval(alarm.getRepeatInterval())) {
			case DAILY:
				repeatType = "daily";
				break;
			case DAY_SPECIFIC:
				repeatType = "day-specific";
				break;
			default:
			case ONCE:
				repeatType = "once";
		}

		String nextAlarm;
		if (!alarm.getActive()) {
			nextAlarm = "never";
		}
		else {
			copyAlarm(alarm);
			nextAlarm = getFieldString(Calendar.DAY_OF_WEEK, true) + ", "
					+ getFieldString(Calendar.MONTH, true)
					+ getFieldString(Calendar.DAY_OF_MONTH, false) + ", "
					+ getFieldString(Calendar.YEAR, false) + ", @ "
					+ getPrettyTime();
			resetData();
		}

		Context ctx = app().getApplicationContext();

		/*
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(ctx)
						.setSmallIcon(R.drawable.ic_alarm)
						.setContentTitle("Alarm has been received!")
						.setContentText("Next alarm: " + nextAlarm + ".");

		if (getAlarmById(alarmId) == null) mBuilder.setContentTitle("Alarm does not exist!");

		NotificationManager mNotificationManager =
				(NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(alarmId, mBuilder.build()); */


		Toast toast = Toast.makeText(ctx, "Next alarm: " + nextAlarm + ".", Toast.LENGTH_SHORT);
		toast.show();
	}

	/**
	 * Returns the PendingIntent for the included alarm
	 * This is the only place where the PendingIntent will be made
	 * - should make cancelling of alarms far more concrete
	 * @param alarm
	 * @return
	 */
	public PendingIntent getAlarmIntent(AlarmModel alarm) {
		Context ctx = BaseApp.app().getApplicationContext();
		HashMap<String, Serializable> data = new HashMap<>();
		data.put("alarmId", alarm.getAlarmId());
		Intent intent = EventBus.intentForEvent(ctx, "AlarmFiredEvent", data);
		intent.putExtra("alarmId", alarm.getAlarmId());
		return PendingIntent.getBroadcast(BaseApp.app().getApplicationContext(), alarm.getAlarmId(), intent, 0);
	}
}
