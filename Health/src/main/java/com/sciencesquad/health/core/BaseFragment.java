package com.sciencesquad.health.core;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sciencesquad.health.R;

import java.util.HashSet;
import java.util.Set;

/**
 * A styled Fragment class with support for DataBinding and easy configuration.
 * Also provides a managed app() and eventBus interface.
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
	@IdRes private int _drawerRes = View.NO_ID;

	/**
	 * The previous Status Bar color to restore later.
	 */
	private int _previousStatus;

	/**
	 * The previous Navigation Bar color to restore later.
	 */
	private int _previousNavigation;

	/**
	 * The saved Binding object for internal usage.
	 */
	private ViewDataBinding _savedBinding;

	/**
	 * The saved themed LayoutInflater for internal usage.
	 */
	private transient LayoutInflater _layoutInflater;

	/**
	 * The internal set of Subscriptions to auto-unsubscribe from.
	 */
	private transient Set<BroadcastReceiver> _subscriptions = new HashSet<>();

	/**
	 * The Configuration class is used to create a BaseFragment.
	 * It allows the Activity to infer more metadata as well.
	 */
	public static class Configuration {
		private String id;
		private String name;
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
		public Configuration(String id, String name, int icon, int theme, int layout) {
			this.id = id;
			this.name = name;
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
		return (T)this._savedBinding;
	}

	/**
	 * Return the themed LayoutInflater for this Fragment.
	 *
	 * @return the themed LayoutInflater for this Fragment
	 */
	protected LayoutInflater getInflater() {
		return this._layoutInflater;
	}

	/**
	 * Returns an Activity-compatible OnClickListener for a Toolbar
	 * to hook into the DrawerLayout above its hierarchy.
	 *
	 * @return an OnClickListener to toggle a DrawerLayout, if available
	 */
	protected View.OnClickListener drawerToggleListener() {
		return v -> {
			if (this._drawerRes != View.NO_ID) {
				DrawerLayout d = (DrawerLayout)getActivity().findViewById(this._drawerRes);
				if (d.isDrawerOpen(GravityCompat.START))
					d.closeDrawer(GravityCompat.START);
				else d.openDrawer(GravityCompat.START);
			}
		};
	}

	/**
	 * Helper to wrap the Application as an Optional type for situations
	 * where it may be ambiguous which Application is the owner of the Context.
	 *
	 * @return the Application as a nullable Optional
	 */
	@NonNull
	protected BaseApp app() {
		return BaseApp.app();
	}

	/**
	 * Helper to wrap the Application EventBus as an Optional type.
	 *
	 * @return the EventBus as a nullable Optional
	 */
	@NonNull
	protected EventBus bus() {
		return this.app().eventBus();
	}

	/**
	 * Tracks any receivers for removal when this Fragment dies.
	 *
	 * @param receiver the receiver for removal
	 */
	protected synchronized void track(@NonNull final BroadcastReceiver receiver) {
		this._subscriptions.add(receiver);
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

		// Ready the fragment state and configuration.
		// TODO: Move animation stuff somewhere else...
		Configuration config = this.getConfiguration();
		this._drawerRes = drawerRes;
		this.onSetupTransition();

		// Return a modified FragmentTransaction for quick usage.
		return transaction
				.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
				.replace(R.id.content, this, config.id)
				.addToBackStack(config.id)
				.setBreadCrumbTitle(config.name);
	}

	/**
	 * Override to set up Fragment transitions properly.
	 */
	public void onSetupTransition() {

	}

	/**
	 * Overridden to support complete binding.
	 */
	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Configuration config = this.getConfiguration();

		// Obtain a new LayoutInflater for the theme specified.
		Context theme = new ContextThemeWrapper(container.getContext(), config.theme);
		this._layoutInflater = inflater.cloneInContext(theme);
		int colors[] = getThemeColors(theme); // primary, primarydark, accent

		if (this._drawerRes != View.NO_ID) {

			// Change the Status Bar color from the DrawerLayout.
			DrawerLayout d = (DrawerLayout)getActivity().findViewById(_drawerRes);
			this._previousStatus = ((ColorDrawable)d.getStatusBarBackgroundDrawable()).getColor();
			d.setStatusBarBackgroundColor(colors[1]);
		} else {

			// Change the Status Bar color directly on the Window.
			this._previousStatus = getActivity().getWindow().getStatusBarColor();
			getActivity().getWindow().setStatusBarColor(colors[1]);
		}

		// Change the Navigation Bar color.
		this._previousNavigation = getActivity().getWindow().getNavigationBarColor();
		//getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.material_black));
		getActivity().getWindow().setNavigationBarColor(colors[1]);

		// Set the current Overview Task Description.
		Bitmap b = drawableToBitmap(theme.getDrawable(config.icon));

		getActivity().setTaskDescription(new ActivityManager.TaskDescription(config.name, b, colors[0]));

		// Return the inflated view and grab our binding.
		this._savedBinding = DataBindingUtil.inflate(this._layoutInflater, config.layout, container, false);
		return this._savedBinding.getRoot();
	}

	/**
	 * Overridden to support restoring the Status and Navigation Bar colors.
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();

		// Restore the correct Status Bar color.
		if (this._drawerRes != View.NO_ID) {
			DrawerLayout d = (DrawerLayout)getActivity().findViewById(_drawerRes);
			d.setStatusBarBackgroundColor(this._previousStatus);
		} else {
			getActivity().getWindow().setStatusBarColor(this._previousStatus);
		}

		// Restore the correct Navigation Bar color.
		getActivity().getWindow().setNavigationBarColor(this._previousNavigation);

		// Restore the previous Activity TaskDescription.
		int c = getThemeColors(getActivity())[0];
		ActivityManager.TaskDescription v = new ActivityManager.TaskDescription(null, null, c);
		getActivity().setTaskDescription(v);

		// Clear out any saved Subscriptions.
		//StreamSupport.stream(this._subscriptions)
		//		.forEach(r -> this.app().map(BaseApp::eventBus).let(bus -> bus.unsubscribe(r)));
		this._subscriptions.clear();
	}
}
