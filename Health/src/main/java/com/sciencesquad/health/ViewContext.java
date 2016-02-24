package com.sciencesquad.health;

import android.app.Activity;
import android.databinding.ViewDataBinding;
import android.os.Bundle;

/**
 *
 * @param <T>
 */
public interface ViewContext<T extends ViewDataBinding>  {
	T getBinding();
	Activity getActivity();

	Bundle getBundle();
	//ViewModelBindingConfig getViewModelBindingConfig();


}
