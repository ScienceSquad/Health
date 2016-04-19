package com.sciencesquad.health.alarm;

import io.realm.RealmObject;

/**
 * Created by andrew on 4/13/16.
 */
public class AlarmModel extends RealmObject {

	private int alarmId;
	private String notificationData;
	private long time;
	private int repeatInterval;
	private boolean repeat;
	private boolean active;
	private int daysOfWeek;

	public void setAlarmId(int alarmId) { this.alarmId = alarmId; }
	public int getAlarmId() { return this.alarmId; }
	public void setNotificationData(String notificationData) { this.notificationData = notificationData; }
	public String getNotificationData() { return this.notificationData; }
	public void setTime(long time) { this.time = time; }
	public long getTime() { return this.time; }
	public void setRepeatInterval(int repeatInterval) { this.repeatInterval = repeatInterval; }
	public int getRepeatInterval() { return this.repeatInterval; }
	public void setRepeat(boolean repeat) { this.repeat = repeat; }
	public boolean getRepeat() { return this.repeat; }
	public void setActive(boolean active) { this.active = active; }
	public boolean getActive() { return this.active; }
	public void setDaysOfWeek(int daysOfWeek) { this.daysOfWeek = daysOfWeek; }
	public int getDaysOfWeek() { return this.daysOfWeek; }
}
