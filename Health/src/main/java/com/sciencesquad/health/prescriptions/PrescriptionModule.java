package com.sciencesquad.health.prescriptions;

import android.util.Log;
import android.util.Pair;

import com.sciencesquad.health.core.alarm.AlarmModule;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import com.sciencesquad.health.core.BaseApp;

import io.realm.RealmResults;

/**
 * Nutrition Module itself.
 *
 * Note:
 * It must be expanded upon from this current baby state
 */
public class PrescriptionModule extends Module {
	private static final String TAG = PrescriptionModule.class.getSimpleName();

	static { Module.registerModule(PrescriptionModule.class); }

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

	public static PrescriptionModule getModule() {
		return Module.moduleForClass(PrescriptionModule.class);
	}

	public PrescriptionModule setName(String name) {
		this.name = name;
		return this;
	}
	public PrescriptionModule setName(PrescriptionModel prescription, String name) {
		if (prescription == null) return this;
		prescriptionRealm.getRealm().beginTransaction();
		prescription.setName(name);
		prescriptionRealm.getRealm().commitTransaction();
		return this;
	}
	public String getName() { return this.name; }
	public PrescriptionModule setDosage(int dosage) {
		this.dosage = dosage;
		return this;
	}
	public PrescriptionModule setDosage(PrescriptionModel prescription, int dosage) {
		if (prescription == null) return this;
		prescriptionRealm.getRealm().beginTransaction();
		prescription.setDosage(dosage);
		prescriptionRealm.getRealm().commitTransaction();
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
		RealmResults<PrescriptionModel> results = getPrescriptions();

		while (results.size() > 0) {
			removePrescription(results.get(0));
			results = getPrescriptions();
		}

		prescriptionRealm.clear();
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {

	}
}
