package com.sciencesquad.health.prescriptions;

import android.util.Log;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.data.RealmContext;
import com.sciencesquad.health.events.BaseApplication;
import io.realm.RealmQuery;

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

	/**
	 * Constructs the module itself.
	 * It also sets up a Realm Context for the Module.
	 */

	public PrescriptionModule() throws Exception{
		Log.d(TAG, "Constructing Prescription Module");
		this.prescriptionRealm = new RealmContext<>();
		this.prescriptionRealm.init(BaseApplication.application(), PrescriptionModel.class, "prescription.realm");
	}

}
