package com.sciencesquad.health.core;

/**
 * An interface for calculating health coefficients
 *  -
 */
public interface Coefficient {

	/**
	 * Calculates module-specific coefficient for use in overview module
	 * @return calculated module coefficient
	 */
	public double calculateCoefficient();

	/**
	 * Sets module-specific coefficient and stores in Realm
	 * @param coefficient specific to module
	 */
	public void setCoefficient(double coefficient);

	/**
	 * Retrieves module-specific coefficient
	 * @return module-specific coefficient
	 */
	public double getCoefficient();
}
