package com.sciencesquad.health.run;


import io.realm.RealmObject;

public class RunModel extends RealmObject {

	/**
	 * TODO: Experiment; if this messes everything up, I will delete these files
	 */

	/**
	 * Run coefficient for overview module
	 */
	private double runCoefficient;

	//
	// GENERATED METHODS FOLLOW:
	//

	public double getRunCoefficient() { return this.runCoefficient; }

	public void setRunCoefficient(double runCoefficient) {
		this.runCoefficient = runCoefficient;
	}
}
