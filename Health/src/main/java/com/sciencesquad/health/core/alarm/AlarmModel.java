package com.sciencesquad.health.core.alarm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by andrew on 4/13/16.
 */
public class AlarmModel extends RealmObject {

	@PrimaryKey
	private int alarmId;

	private long time;
	private int repeatInterval;
	private boolean active;
	private int daysOfWeek;
	private int numDays;

	public void setAlarmId(int alarmId) { this.alarmId = alarmId; }
	public int getAlarmId() { return this.alarmId; }
	public void setTime(long time) { this.time = time; }
	public long getTime() { return this.time; }
	public void setRepeatInterval(int repeatInterval) { this.repeatInterval = repeatInterval; }
	public int getRepeatInterval() { return this.repeatInterval; }
	public void setActive(boolean active) { this.active = active; }
	public boolean getActive() { return this.active; }
	public void setDaysOfWeek(int daysOfWeek) { this.daysOfWeek = daysOfWeek; }
	public int getDaysOfWeek() { return this.daysOfWeek; }
	public void setNumDays(int numDays) { this.numDays = numDays; }
	public int getNumDays() { return this.numDays; }
}
