package com.sciencesquad.health.core;

import android.app.Activity;
import android.databinding.ViewDataBinding;

/**
 * Defines the interaction between a Module and an Activity.
 *
 * @param <T> a generated subclass of ViewDataBinding
 */
public interface ViewContext<T extends ViewDataBinding> {

	/**
	 * The ViewDataBinding that connects the Activity and Module.
	 *
	 * @return the ViewDataBinding that hooks the Activity and Module
	 */
	T binding();

	/**
	 * The Activity in which the Module is displayed.
	 *
	 * @return the Activity in which the Module is displayed
	 */
	Activity activity();
}
