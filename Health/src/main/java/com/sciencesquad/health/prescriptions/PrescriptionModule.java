package com.sciencesquad.health.prescriptions;

import android.util.Log;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import io.realm.RealmResults;

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
	private long repeatDuration;
	private long startDate;

	/**
	 * Constructs the module itself.
	 * It also sets up a Realm Context for the Module.
	 */

	public PrescriptionModule() {
		Log.d(TAG, "Constructing Prescription Module");
		this.prescriptionRealm = new RealmContext<>();
		this.prescriptionRealm.init(BaseApp.app(), PrescriptionModel.class, "prescription.realm");
	}

	public void setName(String name) { this.name = name; }
	public String getName() { return this.name; }
	public void setDosage(int dosage) { this.dosage = dosage; }
	public int getDosage() { return this.dosage; }
	public void setRepeatDuration(long repeatDuration) { this.repeatDuration = repeatDuration; }
	public long getRepeatDuration() { return this.repeatDuration; }
	public void setStartDate(long startDate) { this.startDate = startDate; }
	public long getStartDate() { return this.startDate; }

	public void addPrescription() {
		PrescriptionModel prescriptionModel = new PrescriptionModel();
		prescriptionModel.setName(this.name);
		prescriptionModel.setDosage(this.dosage);
		prescriptionModel.setRepeatDuration(this.repeatDuration);
		prescriptionModel.setStartDate(this.startDate);
		prescriptionRealm.add(prescriptionModel);
	}

	public RealmResults<PrescriptionModel> getPrescriptions() {
		return prescriptionRealm.query(PrescriptionModel.class).findAll();
	}

	public void removePrescription(PrescriptionModel item) {
		prescriptionRealm.getRealm().beginTransaction();
		item.removeFromRealm();
		prescriptionRealm.getRealm().commitTransaction();
	}

	public void clearAllPrescriptions() {
		prescriptionRealm.clear();
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {

	}
}
