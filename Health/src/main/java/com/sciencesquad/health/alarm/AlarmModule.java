package com.sciencesquad.health.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;

import org.json.JSONException;
import org.json.JSONObject;

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
	private JSONObject notificationData;

	public enum RepeatInterval {
		NEVER,
		DAILY,
		DAY_SPECIFIC
	}

	private Calendar time;
	private RepeatInterval repeatInterval;
	private int alarmId;
	private ArrayList<Integer> daysOfWeek;

	private final int DEFAULT_REPEAT = 1;

	public AlarmModule() {
		this.alarmRealm = new RealmContext<>();
		this.alarmRealm.init(BaseApp.app(), AlarmModel.class, "alarm.realm");

		resetData();
	}

	private void resetData() {
		this.notificationData = null;
		this.time = Calendar.getInstance();
		this.time.set(Calendar.SECOND, 0);
		this.time.set(Calendar.MILLISECOND, 0);
		this.repeatInterval = RepeatInterval.NEVER;
		this.alarmId = -1;
		this.daysOfWeek = new ArrayList<>();
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

		return alarmId;
	}

	/**
	 * Setting notification data
	 * In case you want a notification to be sent when the alarm fires
	 */

	public AlarmModule setNotificationTitle(String title) {
		if (notificationData == null) {
			notificationData = new JSONObject();
		}
		try {
			notificationData.put("title", title);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return this;
	}

	public AlarmModule setNotificationContent(String content) {
		if (notificationData == null) {
			notificationData = new JSONObject();
		}
		try {
			notificationData.put("content", content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return this;
	}

	public AlarmModule setNotificationIcon(int icon) {
		if (notificationData == null) {
			notificationData = new JSONObject();
		}
		try {
			notificationData.put("icon", icon);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * If you're going to make a custom layout and intend to include a title and content,
	 * be sure to include TextViews with id's "title" and "content"
	 * @param layout
	 */
	public AlarmModule setNotificationLayout(int layout) {
		if (notificationData == null) {
			notificationData = new JSONObject();
		}
		try {
			notificationData.put("layout", layout);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return this;
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

		PendingIntent pendingIntent = getAlarmIntent(alarm);

		AlarmManager alarmMgr = (AlarmManager) BaseApp.app()
				.getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);

		this.copyAlarm(alarm);

		if (repeatInterval == RepeatInterval.DAY_SPECIFIC) {
			int dayOfWeek = this.get(Calendar.DAY_OF_WEEK);
			if (this.daysOfWeek.contains(dayOfWeek)) {
				alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, this.getTimeInMillis(), 7 * AlarmManager.INTERVAL_DAY, pendingIntent);
			}
			Calendar now = Calendar.getInstance();
			now.get(Calendar.DAY_OF_WEEK);
		}
		else if (repeatInterval == RepeatInterval.DAILY) {
			alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, this.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
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
		if (this.alarmId > -1) {
			AlarmModel alarm = getAlarmById(this.alarmId);
			this.resetData();
			return alarm;
		}

		AlarmModel alarm = new AlarmModel();

		alarm.setAlarmId(getNextAlarmId());

		if (notificationData != null) alarm.setNotificationData(notificationData.toString());
		else alarm.setNotificationData("");

		alarm.setRepeatInterval(repeatIntervalToInt(repeatInterval));
		alarm.setDaysOfWeek(arrayListToInt(daysOfWeek));
		alarm.setTime(time.getTimeInMillis());
		alarm.setActive(true);

		this.alarmRealm.add(alarm);

		this.resetData();

		sendAlarm(alarm);

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
		return results.get(0);
	}

	public void copyAlarm(AlarmModel alarm) {
		this.resetData();
		this.time.setTimeInMillis(alarm.getTime());
		this.repeatInterval = intToRepeatInterval(alarm.getRepeatInterval());
		this.alarmId = alarm.getAlarmId();
		this.daysOfWeek = intToArrayList(alarm.getDaysOfWeek());
	}

	public AlarmModule setAlarmById(int alarmId) {
		AlarmModel alarm = getAlarmById(alarmId);
		copyAlarm(alarm);
		return this;
	}

	/**
	 * Returns the PendingIntent for the included alarm
	 * This is the only place where the PendingIntent will be made
	 * - should make cancelling of alarms far more concrete
	 * @param alarm
	 * @return
	 */
	public PendingIntent getAlarmIntent(AlarmModel alarm) {
		Intent intent = new Intent();
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
