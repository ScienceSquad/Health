package com.sciencesquad.health.prescriptions;

import io.realm.RealmObject;

/**
 * Created by andrew on 3/3/16.
 */
public class PrescriptionModel extends RealmObject {

	private String name;
	private int dosage;
	private int alarmID;
	private boolean affectsSleeping;

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

	public void setAlarmID(int alarmID) {
		this.alarmID = alarmID;
	}

	public int getAlarmID() {
		return this.alarmID;
	}

	public boolean affectsSleeping() {
		return affectsSleeping;
	}

	public void setAffectsSleeping(boolean affectsSleeping) {
		this.affectsSleeping = affectsSleeping;
	}
}
