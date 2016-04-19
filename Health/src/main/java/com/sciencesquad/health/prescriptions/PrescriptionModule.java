package com.sciencesquad.health.prescriptions;

import android.util.Log;
import android.util.Pair;

import com.sciencesquad.health.alarm.AlarmModel;
import com.sciencesquad.health.alarm.AlarmModule;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import com.sciencesquad.health.core.BaseApp;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import java.util.Calendar;

/**
 * Nutrition Module itself.
 *
 * Note:
 * It must be expanded upon from this current baby state
 */
public class PrescriptionModule extends Module {
	private static final String TAG = PrescriptionModule.class.getSimpleName();

	private RealmContext<PrescriptionModel> prescriptionRealm;

	private String name;
	private int dosage;
	private int alarmID;

	/**
	 * Constructs the module itself.
	 * It also sets up a Realm Context for the Module.
	 */

	public PrescriptionModule() {
		Log.d(TAG, "Constructing Prescription Module");
		this.prescriptionRealm = new RealmContext<>();
		this.prescriptionRealm.init(BaseApp.app(), PrescriptionModel.class, "prescription.realm");
	}

	public PrescriptionModule setName(String name) {
		this.name = name;
		return this;
	}
	public String getName() { return this.name; }
	public PrescriptionModule setDosage(int dosage) {
		this.dosage = dosage;
		return this;
	}
	public int getDosage() { return this.dosage; }
	public PrescriptionModule setAlarmID(int alarmID) {
		this.alarmID = alarmID;
		return this;
	}
	public int getAlarmID() { return this.alarmID; }

	public PrescriptionModel addPrescription() {
		PrescriptionModel prescriptionModel = new PrescriptionModel();
		prescriptionModel.setName(this.name);
		prescriptionModel.setDosage(this.dosage);
		prescriptionModel.setAlarmID(this.alarmID);
		prescriptionRealm.add(prescriptionModel);
		return prescriptionModel;
	}

	public RealmResults<PrescriptionModel> getPrescriptions() {
		return prescriptionRealm.query(PrescriptionModel.class).findAll();
	}

	public void removePrescription(PrescriptionModel item) {
		AlarmModule alarmModule = new AlarmModule();

		alarmModule.removeAlarmById(item.getAlarmID());

		prescriptionRealm.getRealm().beginTransaction();
		item.removeFromRealm();
		prescriptionRealm.getRealm().commitTransaction();
	}

	public void clearAllPrescriptions() {
		prescriptionRealm.clear();
	}


	@Override
	public Pair<String, Integer> identifier() {
		return null;
	}

	@Override
	public void init() {

	}
}
