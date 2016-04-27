package com.sciencesquad.health.core;

/**
 * An interface for calculating health coefficients
 *  -
 */
public interface Coefficient {

	/**
	 * Calculates module-dependent coefficient and stores in Realm
	 */
	public void calculateCoefficient();

}
