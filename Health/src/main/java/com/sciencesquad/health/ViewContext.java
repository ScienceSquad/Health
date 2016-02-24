package com.sciencesquad.health;

import android.app.Activity;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;

/**
 * Defines the interaction between a Module and its View.
 *
 * @param <T> a generated subclass of ViewDataBinding
 */
public interface ViewContext<T extends ViewDataBinding> {

	/**
	 * The ViewDataBinding that hooks the View and ViewModel.
	 * @return the ViewDataBinding that hooks the View and the ViewModel
	 */
	T binding();

	/**
	 * The Activity in which the View is displayed.
	 * @return the Activity in which the View is displayed
	 */
	Activity activity();

	/**
	 * The options bundle for the View.
	 * @return the options bundle for the View
	 */
	Bundle bundle();

	/**
	 * The layout resource ID.
	 * @return the layout resource ID
	 */
	@LayoutRes int layout();
}
