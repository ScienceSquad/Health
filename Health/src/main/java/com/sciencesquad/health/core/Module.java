package com.sciencesquad.health.core;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.sciencesquad.health.core.util.X;
import java8.util.stream.StreamSupport;
import rx.Subscription;
import rx.functions.Action1;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * The Module abstract class is the binding glue behind a modular architecture
 * that relies on dependency injection and a global and/or local event bus.
 *
 *
 * Any subclass of the Module class should register itself and observe for any
 * relevant events (such as the AppCreateEvent or ActivityStartEvent). It may
 * then request resources like a SensorContext or DataContext to support a data
 * back-end. It is its own ViewModel, so it may also request a
 */
public abstract class Module implements Observable {
	private static final String TAG = "Module";

	/**
	 * A collection of all the registered modules; a module may not be
	 * registered more than once.
	 */
	private static Set<Module> _modules = new HashSet<>();

	/**
	 * The internal set of Subscriptions to auto-unsubscribe from.
	 */
	private transient Set<Subscription> _subscriptions = new HashSet<>();

	/**
	 * Allows the Module subclass to act as an Observable, and
	 * therefore its own ViewModel component (which is ideal, since it
	 * already fulfills the Controller requirement of MVC and now MVVM).
	 */
	private transient PropertyChangeRegistry _callbacks;

	/**
	 * Special identifier only set by a ContextBinder for identification.
	 */
	/*package*/ transient UUID _identifier;

	/**
	 *
	 */
	/*package*/ ViewContext<? extends ViewDataBinding> _viewContext;

	/**
	 *
	 */
	/*package*/ ViewContext<? extends ViewDataBinding> _dataContext;

	/**
	 *
	 */
	/*package*/ ViewContext<? extends ViewDataBinding> _sensorContext;

	/**
	 * Any subclass of the Module class should ideally register itself
	 * for free support from the framework dependency injector.
	 *
	 * Example:
	 * ```java
	 * static {
	 *     Module.registerModule(this);
	 * }
	 * ```
	 *
	 * @param module the module to register
	 * @return true if registration successful, false otherwise
	 */
	public static <T extends Module> boolean registerModule(@NonNull Class<T> module) {
		try {
			T instance = module.newInstance();
			return _modules.add(instance);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Unregister.
	 *
	 * @param module the module to unregister
	 * @return true if unregistration successful, false otherwise
	 */
	public static <T extends Module> boolean unregisterModule(@NonNull Class<T> module) {
		return _modules.remove(module);
	}

	/**
	 * Returns all registered subclasses of Module.
	 *
	 * @return all registered Module subclasses
	 */
	@NonNull
	public static Set<Module> registeredModules() {
		return _modules;
	}

	/**
	 * A Module's identifier provides the name and icon of a Module for
	 * implementations where the user interacts with available Modules.
	 *
	 * The identifier is a tuple consisting of a String for the Module name,
	 * and an icon, represented as an integer drawable resource.
	 *
	 * @return a tuple of a String for the name and int for the drawable
	 */
	public abstract Pair<String, Integer> identifier();

	/**
	 * Refrain from using the constructor of a module for initialization
	 * procedure. Module instances may be constructed and destroyed randomly.
	 *
	 * This method is the preferred point of initialization and is called
	 * by the system when the module is to be created, only once.
	 */
	public abstract void init();

	/**
	 *
	 *
	 * @param <T>
	 * @return
	 */
	protected <T extends ViewDataBinding> X<ViewContext<T>> viewContext() {
		return X.of(null);
	}

	/**
	 *
	 *
	 * @param <T>
	 * @return
	 */
	protected <T extends ViewDataBinding> X<ViewContext<T>> dataContext() {
		return X.of(null);
	}

	/**
	 *
	 *
	 * @param <T>
	 * @return
	 */
	protected <T extends ViewDataBinding> X<ViewContext<T>> sensorContext() {
		return X.of(null);
	}

	/**
	 * Publishes any Events to the shared app EventBus.
	 *
	 * @param event the event to publish
	 * @param <E> the type of Event being published
	 */
	public synchronized <E extends Event> void publish(@NonNull E event) {
		this.app().map(BaseApp::eventBus).let(bus -> bus.publish(event));
	}

	/**
	 * Subscribes and auto-manage a Subscription to an Event.
	 * Automatically uses the shared app EventBus.
	 *
	 * @implNote Relies on the invocation of finalize() to clean up.
	 *
	 * @param eventClass the type of Event subscribed to
	 * @param handler the action to perform upon notification
	 * @param <E> the type of Event being subscribed to
	 */
	public synchronized <E extends Event> void subscribe(@NonNull final Class<E> eventClass,
														 @Nullable final Object source, @NonNull final Action1<E> handler) {
		this.app().map(BaseApp::eventBus).let(bus -> {
			Subscription sub = bus.subscribe(eventClass, source, handler);
			this._subscriptions.add(sub);
		});
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
		StreamSupport.stream(this._subscriptions).forEach(Subscription::unsubscribe);
		this._subscriptions.clear();
	}

	/**
	 * Helper to wrap the Application as an Optional type for situations
	 * where it may be ambiguous which Application is the owner of the Context.
	 *
	 * @return the Application as a nullable Optional
	 */
	@NonNull
	protected X<BaseApp> app() {
		return X.of(BaseApp.app());
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