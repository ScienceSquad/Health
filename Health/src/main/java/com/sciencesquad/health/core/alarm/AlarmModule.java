package com.sciencesquad.health.core.alarm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import com.sciencesquad.health.prescriptions.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import io.realm.RealmResults;

/**
 * Created by andrew on 4/13/16.
 */
public class AlarmModule extends Module {
	static final String TAG = AlarmModule.class.getSimpleName();
	static final String ALARM_ID_FIELD = "alarmId";

	static { Module.registerModule(AlarmModule.class); }


	private RealmContext<AlarmModel> alarmRealm;

	public enum RepeatInterval {
		NEVER,
		DAILY,
		DAY_SPECIFIC
	}

	private Calendar time;
	private RepeatInterval repeatInterval;
	private int alarmId;
	private ArrayList<Integer> daysOfWeek;
	private boolean active;

	private final int DEFAULT_REPEAT = 1;

	public AlarmModule() {
		this.alarmRealm = new RealmContext<>();
		this.alarmRealm.init(BaseApp.app(), AlarmModel.class, "alarm.realm");

		resetData();
	}

	public static AlarmModule getModule() {
		return Module.moduleForClass(AlarmModule.class);
	}

	private void resetData() {
		this.time = Calendar.getInstance();
		this.time.set(Calendar.SECOND, 0);
		this.time.set(Calendar.MILLISECOND, 0);
		this.repeatInterval = RepeatInterval.NEVER;
		this.alarmId = -1;
		this.daysOfWeek = new ArrayList<>();
		this.active = true;
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

	public void sendAlarm(AlarmModel alarm) {

		if (!alarm.getActive()) return;

		PendingIntent pendingIntent = getAlarmIntent(alarm);

		AlarmManager alarmMgr = (AlarmManager) BaseApp.app()
				.getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);

		this.copyAlarm(alarm);


		switch (repeatInterval) {
			case DAY_SPECIFIC:
				int dayOfWeek = this.get(Calendar.DAY_OF_WEEK);
				if (this.daysOfWeek.contains(dayOfWeek)) {
					alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, this.getTimeInMillis(), 7 * AlarmManager.INTERVAL_DAY, pendingIntent);
				}
				Calendar now = Calendar.getInstance();
				now.get(Calendar.DAY_OF_WEEK);
				break;
			case DAILY:
				alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, this.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
				break;
			case NEVER:
			default:
				alarmMgr.set(AlarmManager.RTC_WAKEUP, this.getTimeInMillis(), pendingIntent);
				break;
		}
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
			default: return RepeatInterval.NEVER;
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

		if (this.alarmId > -1) {
			this.alarmRealm.getRealm().commitTransaction();
			sendAlarm(alarm);
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

	public AlarmModule setRepeatInterval(AlarmModel alarm, RepeatInterval repeatInterval) {
		if (alarm == null) return this;
		cancelAlarm(alarm);
		alarmRealm.getRealm().beginTransaction();
		alarm.setRepeatInterval(repeatIntervalToInt(repeatInterval));
		alarmRealm.getRealm().commitTransaction();
		sendAlarm(alarm);
		return this;
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

	public AlarmModule setRepeatInterval(int alarmId, RepeatInterval repeatInterval) {
		return setRepeatInterval(getAlarmById(alarmId), repeatInterval);
	}

	public void copyAlarm(AlarmModel alarm) {
		this.resetData();
		this.time.setTimeInMillis(alarm.getTime());
		this.repeatInterval = intToRepeatInterval(alarm.getRepeatInterval());
		this.alarmId = alarm.getAlarmId();
		this.daysOfWeek = intToArrayList(alarm.getDaysOfWeek());
		this.active = alarm.getActive();
	}

	public AlarmModule setAlarmById(int alarmId) {
		AlarmModel alarm = getAlarmById(alarmId);
		copyAlarm(alarm);
		return this;
	}

	public void handleAlarm(int alarmId) {

		Context ctx = BaseApp.app().getApplicationContext();

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(ctx)
						.setSmallIcon(R.drawable.ic_alarm)
						.setContentTitle("Alarm has been received!")
						.setContentText("Alarm ID: " + String.valueOf(alarmId));

		if (getAlarmById(alarmId) == null) mBuilder.setContentTitle("Alarm does not exist!");

		NotificationManager mNotificationManager =
				(NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(alarmId, mBuilder.build());
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
		Intent intent = new Intent(ctx, AlarmReceiver.class);
		intent.putExtra("alarmId", alarm.getAlarmId());
		return PendingIntent.getBroadcast(BaseApp.app().getApplicationContext(), alarm.getAlarmId(), intent, 0);
	}

	@Override
	public Pair<String, Integer> identifier() {
		return null;
	}

	@Override
	public void init() {

	}
}
