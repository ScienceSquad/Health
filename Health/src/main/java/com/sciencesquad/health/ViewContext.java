package com.sciencesquad.health;

import android.app.Activity;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;

/**
 *
 * @param <T>
 */
public interface ViewContext<T extends ViewDataBinding>  {
	T binding();
	Activity activity();
	Bundle bundle();
	@LayoutRes int layout();
}
