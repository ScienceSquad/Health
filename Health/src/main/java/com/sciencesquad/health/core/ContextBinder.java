package com.sciencesquad.health.core;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.sciencesquad.health.R;
import com.sciencesquad.health.ViewContext;

import java.util.UUID;

/**
 * The core class of the framework connecting the Activity with the
 * Module and incorporating DataBinding. The Activity should call and
 * pass appropriate callbacks to this instance.
 *
 * @param <T> the Module type
 */
public class ContextBinder<T extends Module> {
	private static final String TAG = ContextBinder.class.getSimpleName();

	/**
	 *
     */
	public void bind(final @NonNull Activity activity, final @Nullable Bundle savedInstanceState,
					 final @NonNull ViewDataBinding dataBinding, final @NonNull Module module,
					 final @LayoutRes int layoutRes) {
		DataBindingUtil.setContentView(activity, layoutRes);

		// Assign or retrieve the module's UUID.
		if(savedInstanceState == null) {
			module._identifier = UUID.randomUUID();
		} else {
			String id = savedInstanceState.getString("__context_binder_" + module.getClass().getName());
			module._identifier = UUID.fromString(id);
		}

		// Apply a new transient ViewContext to represent this transaction.
		module._viewContext = new ViewContext<ViewDataBinding>() {
			@Override public ViewDataBinding binding() {
				return dataBinding;
			}
			@Override public Activity activity() {
				return activity;
			}
			@Override public Bundle bundle() {
				return savedInstanceState;
			}
			@Override public int layout() {
				return layoutRes;
			}
		};

		dataBinding.setVariable(R.id.dataBinding, module);
		// CALLBACK HERE: onViewAttached
	}

	/**
	 *
	 */
	public void unbind(final @NonNull Module module, final @NonNull ViewDataBinding dataBinding) {
		dataBinding.setVariable(R.id.dataBinding, null);
		module._viewContext = null;
	}
}