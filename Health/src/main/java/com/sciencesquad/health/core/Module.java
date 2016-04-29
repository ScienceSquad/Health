package com.sciencesquad.health.core;

import android.content.BroadcastReceiver;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.*;

/**
 * The Module abstract class is the binding glue behind a modular architecture
 * that relies on dependency injection and a global and/or local event bus.
 *
 *
 * Any subclass of the Module class should start itself and observe for any
 * relevant events (such as the AppCreateEvent or ActivityStartEvent). It may
 * then request resources like a SensorContext or DataContext to support a data
 * back-end. It is its own ViewModel, so it may also request a
 */
public abstract class Module implements Observable, Coefficient {
	private static final String TAG = "Module";

	/**
	 * A collection of all the registered modules; a module may not be
	 * registered more than once.
	 */
	/*package*/ static HashMap<Class<? extends Module>, Module> _modules = new HashMap<>();

	/**
	 * The internal set of Subscriptions to auto-unsubscribe from.
	 */
	private transient Set<BroadcastReceiver> _subscriptions = new HashSet<>();

	/**
	 * Allows the Module subclass to act as an Observable, and
	 * therefore its own ViewModel component (which is ideal, since it
	 * already fulfills the Controller requirement of MVC and now MVVM).
	 */
	private transient PropertyChangeRegistry _callbacks;

	/**
	 * Any subclass of the Module class should ideally start itself
	 * for free support from the framework dependency injector.
	 *
	 * Example:
	 * ```java
	 * static {
	 *     Module.start(this);
	 * }
	 * ```
	 *
	 * @param module the module to start
	 * @return true if registration successful, false otherwise
	 */
	@Nullable
	public static <T extends Module> T start(@NonNull Class<T> module) {
		if (_modules.containsKey(module))
			return null;

		try {
			Log.i(TAG, "Initializing " + module.getSimpleName() + "...");
			T instance = module.newInstance();
			instance.onStart();
			_modules.put(module, instance);
			return instance;
		} catch (Exception e) {
			Log.e(TAG, "Unable to start Module class! " + e.getLocalizedMessage());
			return null;
		}
	}

	/**
	 * Unregister.
	 *
	 * @param module the module to stop
	 * @return true if unregistration successful, false otherwise
	 */
	public static <T extends Module> void stop(@NonNull Class<T> module) {
		_modules.remove(module);
	}

	/**
	 * Returns all registered subclasses of Module.
	 *
	 * @return all registered Module subclasses
	 */
	@NonNull
	public static Collection<Module> all() {
		return Collections.unmodifiableCollection(_modules.values());
	}

	/**
	 * Get the registered Module for the given Class.
	 * Note: will create one if it does not exist.
	 *
	 * @param module the Class of Module that was registered
	 * @param <T> a Module subclass
	 * @return the registered Module for the given Class
	 */
	@NonNull
	@SuppressWarnings("unchecked")
	public static <T extends Module> T of(@NonNull Class<T> module) {
		if (_modules.containsKey(module))
			return (T)_modules.get(module);
		else return start(module);
	}

	/**
	 * Refrain from using the constructor of a module for initialization
	 * procedure. Module instances may be constructed and destroyed randomly.
	 *
	 * This method is the preferred point of initialization and is called
	 * by the system when the module is to be created, only once.
	 */
	public abstract void onStart();

	/**
	 * This method is invoked when the Module is required to be stopped
	 * by the System or Android, and should clear up resources used.
	 */
	public abstract void onStop();

	/**
	 * Tracks any receivers for removal when this Fragment dies.
	 *
	 * @param receiver the receiver for removal
	 */
	protected synchronized void track(@NonNull final BroadcastReceiver receiver) {
		this._subscriptions.add(receiver);
	}

	/**
	 * Invoked upon garbage collection or deallocation of the Module.
	 * If the current Runtime does not invoke this method automatically,
	 * it MUST be invoked manually, to ensure Subscriptions are dealt with.
	 *
	 * @throws Throwable a generic error
	 */
	@Override
	protected synchronized void finalize() throws Throwable {
		super.finalize();
		//StreamSupport.stream(this._subscriptions)
		//		.forEach(r -> this.app().map(BaseApp::eventBus).let(bus -> bus.unsubscribe(r)));
		this._subscriptions.clear();
	}

	/**
	 * Helper to wrap the Application as an Optional type for situations
	 * where it may be ambiguous which Application is the owner of the Context.
	 *
	 * @return the Application
	 */
	@NonNull
	protected BaseApp app() {
		return BaseApp.app();
	}

	/**
	 * Helper to wrap the Application EventBus.
	 *
	 * @return the EventBus
	 */
	@NonNull
	protected EventBus bus() {
		return this.app().eventBus();
	}

	/**
	 * Support for Observable.
	 * @see android.databinding.BaseObservable
	 * @param callback the OnPropertyChangedCallback callback
	 */
	@Override
	public synchronized void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
		if (_callbacks == null) {
			_callbacks = new PropertyChangeRegistry();
		}
		_callbacks.add(callback);
	}

	/**
	 * Support for Observable.
	 * @see android.databinding.BaseObservable
	 * @param callback the OnPropertyChangedCallback callback
	 */
	@Override
	public synchronized void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
		if (_callbacks != null) {
			_callbacks.remove(callback);
		}
	}

	/**
	 * Notifies listeners that all properties of this instance have changed.
	 */
	public synchronized void notifyChange() {
		if (_callbacks != null) {
			_callbacks.notifyCallbacks(this, 0, null);
		}
	}

	/**
	 * Notifies listeners that a specific property has changed. The getter for the property
	 * that changes should be marked with {@link Bindable} to generate a field in
	 * `BR` to be used as `fieldId`.
	 *
	 * @param fieldId The generated BR id for the Bindable field.
	 */
	public void notifyPropertyChanged(int fieldId) {
		if (_callbacks != null) {
			_callbacks.notifyCallbacks(this, fieldId, null);
		}
	}
}