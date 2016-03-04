package com.sciencesquad.health.prescriptions;

import com.sciencesquad.health.alarm.AlarmSender;

import io.realm.RealmObject;

/**
 * Created by andrew on 3/3/16.
 */
public class PrescriptionModel extends RealmObject {

	private String name;
	private int dosage;
	private long repeatDuration;
	private long startDate;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setDosage(int dosage) {
		this.dosage = dosage;
	}

	public int getDosage() {
		return this.dosage;
	}

	public void setRepeatDuration(long repeatDuration) {
		this.repeatDuration = repeatDuration;
	}

	public long getRepeatDuration() {
		return this.repeatDuration;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getStartDate() {
		return this.startDate;
	}
}
