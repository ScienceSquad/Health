package com.sciencesquad.health.prescriptions;

import android.util.Log;
import android.util.Pair;

import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import com.sciencesquad.health.core.BaseApp;
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
		this.prescriptionRealm.init(BaseApp.app(), PrescriptionModel.class, "prescription.realm");
	}

	@Override
	public Pair<String, Integer> identifier() {
		return null;
	}

	@Override
	public void init() {

	}
}
