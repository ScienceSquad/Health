package com.sciencesquad.health.run;

import com.sciencesquad.health.core.Coefficient;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import com.sciencesquad.health.sleep.SleepDataModel;

public class RunModule extends Module implements Coefficient {
	public static final String TAG = RunModule.class.getSimpleName();
	private static final String REALMNAME = "run.realm";

	/**
	 * The RunModule-specific RealmContext for storing run info
	 *
	 * TODO: if this doesn't work I will nuke this commit or these files
	 */
	private RealmContext<RunModel> realmContext;

	/**
	 * Run Coefficients
	 */
	private double runCoefficient;

	/**
	 * Calculates run coefficient for use in overview module
	 * @return calculated run coefficient
	 */
	public double calculateCoefficient() {
		return 0;
	}

	/**
	 * Retrieves run coefficient
	 * @return runCoefficient
	 */
	@Override
	public double getCoefficient() {
		return this.runCoefficient;
	}

	/**
	 * Calculates run coefficient
	 * TODO: Implement!
	 * @param coefficient
	 * @see Coefficient
	 */
	@Override
	public void setCoefficient(double coefficient) {
		this.runCoefficient = coefficient;
	}

	@Override
	public void onStart() {

		// Initialize and acquire the Sleep database.
		this.realmContext = new RealmContext<>();
		this.realmContext.init(app(), RunModel.class, REALMNAME);

		setCoefficient(0);

	}

	@Override
	public void onStop() {

	}
}
