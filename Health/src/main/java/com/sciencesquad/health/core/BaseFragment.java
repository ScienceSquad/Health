package com.sciencesquad.health.core;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.*;
import android.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Property;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sciencesquad.health.R;

/**
 * A styled Fragment class with support for DataBinding and easy configuration.
 *
 * Requirements:
 * 	- Support DataBinding in the layout XML file.
 * 	- Provide a custom theme extending one of AppCompat's themes.
 * 	- Provide a Module class to service the backend.
 */
public abstract class BaseFragment extends Fragment {
	public static final String TAG = BaseFragment.class.getSimpleName();

	/**
	 * The DrawerLayout ID, if it exists. Used to custom-style the Status Bar.
	 */
	@IdRes private int drawerRes = View.NO_ID;

	/**
	 * The previous Status Bar color to restore later.
	 */
	private int previousStatus;

	/**
	 * The previous Navigation Bar color to restore later.
	 */
	private int previousNavigation;

	/**
	 * The saved Binding object for internal usage.
	 */
	private ViewDataBinding savedBinding;

	/**
	 * The Configuration class is used to create a BaseFragment.
	 * It allows the Activity to infer more metadata as well.
	 */
	public static class Configuration {
		private String id;
		private String name;
		Class<? extends Module> moduleClass;
		@DrawableRes private int icon;
		@StyleRes private int theme;
		@LayoutRes private int layout;

		/**
		 * Create a Configuration from which to auto-create the Fragment.
		 *
		 * @param id the Fragment's internal ID
		 * @param name the user-friendly name of the Fragment
		 * @param icon the user-friendly icon of the Fragment
		 * @param theme the Fragment theme to apply
		 * @param layout the Fragment layout to inflate
		 */
		public Configuration(String id, String name, Class<? extends Module> moduleClass,
							 int icon, int theme, int layout) {
			this.id = id;
			this.name = name;
			this.moduleClass = moduleClass;
			this.icon = icon;
			this.theme = theme;
			this.layout = layout;
		}
	}

	/**
	 * Return the styled color attributes for the given Context.
	 * That is, colorPrimary, colorPrimaryDark, and colorAccent.
	 *
	 * @param ctx the context to search
	 * @return the three color attributes for this context
	 */
	@SuppressLint("PrivateResource")
	public static int[] getThemeColors(Context ctx) {
		TypedValue typedValue = new TypedValue();
		ctx.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
		int colorPrimary = typedValue.data;
		ctx.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
		int colorPrimaryDark = typedValue.data;
		ctx.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
		int colorAccent = typedValue.data;
		return new int [] { colorPrimary, colorPrimaryDark, colorAccent };
	}

	/**
	 * Return a Bitmap from the contents of this Drawable.
	 *
	 * @param drawable the drawable to transform
	 * @return the transformed bitmap
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable)drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	/**
	 * Obtain this Fragment's configuration.
	 *
	 * @return the configuration for this fragment
	 */
	protected abstract Configuration getConfiguration();

	/**
	 * Shorthand to access the generated Binding.
	 *
	 * @param <T> a generated Binding class
	 * @return the generated Binding object
	 */
	@SuppressWarnings("unchecked")
	protected <T extends ViewDataBinding> T xml() {
		return (T)this.savedBinding;
	}

	/**
	 * Show this Fragment from FragmentTransaction.
	 * If the drawerRes is provided, it is used to style a transparent
	 * status bar above a NavigationView, else, the status bar itself.
	 *
	 * @param transaction the FragmentTransaction
	 * @param drawerRes the DrawerLayout of the Activity, if it exists
	 * @return the FragmentTransaction used
	 */
	public FragmentTransaction open(FragmentTransaction transaction, @IdRes int drawerRes) {
		Configuration config = this.getConfiguration();
		this.drawerRes = drawerRes;
		return transaction
				.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
				.replace(R.id.content, this, config.id)
				.addToBackStack(config.id)
				.setBreadCrumbTitle(config.name);
	}

	/**
	 * Overridden to support complete binding.
	 */
	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Configuration config = this.getConfiguration();

		// Obtain a new LayoutInflater for the theme specified.
		Context theme = new ContextThemeWrapper(container.getContext(), config.theme);
		LayoutInflater local = inflater.cloneInContext(theme);
		int colors[] = getThemeColors(theme); // primary, primarydark, accent

		if (this.drawerRes != View.NO_ID) {

			// Change the Status Bar color from the DrawerLayout.
			DrawerLayout d = (DrawerLayout)getActivity().findViewById(drawerRes);
			this.previousStatus = ((ColorDrawable)d.getStatusBarBackgroundDrawable()).getColor();
			d.setStatusBarBackgroundColor(colors[1]);
		} else {

			// Change the Status Bar color directly on the Window.
			this.previousStatus = getActivity().getWindow().getStatusBarColor();
			getActivity().getWindow().setStatusBarColor(colors[1]);
		}

		// Change the Navigation Bar color.
		this.previousNavigation = getActivity().getWindow().getNavigationBarColor();
		getActivity().getWindow().setNavigationBarColor(colors[1]);

		// Set the current Overview Task Description.
		Bitmap b = drawableToBitmap(theme.getDrawable(config.icon));
		getActivity().setTaskDescription(new ActivityManager.TaskDescription(config.name, b, colors[0]));

		// Return the inflated view and grab our binding.
		this.savedBinding = DataBindingUtil.inflate(local, config.layout, container, false);
		View root = this.savedBinding.getRoot();
		// FIXME: Assign Module here.
		return root;
	}

	/**
	 * Overridden to support restoring the Status and Navigation Bar colors.
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();

		// Restore the correct Status Bar color.
		if (this.drawerRes != View.NO_ID) {
			DrawerLayout d = (DrawerLayout)getActivity().findViewById(drawerRes);
			d.setStatusBarBackgroundColor(this.previousStatus);
		} else {
			getActivity().getWindow().setStatusBarColor(this.previousStatus);
		}

		// Restore the correct Navigation Bar color.
		getActivity().getWindow().setNavigationBarColor(this.previousNavigation);
	}
}
