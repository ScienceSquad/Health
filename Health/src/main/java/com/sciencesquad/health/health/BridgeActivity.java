package com.sciencesquad.health.health;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

public class BridgeActivity extends AppCompatActivity {
	private static final String TAG = "BridgeActivity";

	/**
	 * The Activity was created.
	 */
	public static final class CreateEvent extends Event {

		/**
		 * The saved instance state of this Activity.
		 */
		// FIXME: Probably should not be broadcasting this...
		public Bundle savedInstanceState;

		/**
		 * The persistent state of this Activity.
		 */
		// FIXME: Probably should not be broadcasting this...
		public PersistableBundle persistentState;
	}

	/**
	 * The Activity was destroyed.
	 */
	public static final class DestroyEvent extends Event {
		// EMPTY
	}

	/**
	 * The Activity was paused.
	 */
	public static final class PauseEvent extends Event {
		// EMPTY
	}

	/**
	 * The Activity was resumed.
	 */
	public static final class ResumeEvent extends Event {
		// EMPTY
	}

	/**
	 * The Activity was started.
	 */
	public static final class StartEvent extends Event {
		// EMPTY
	}

	/**
	 * The Activity was stopped.
	 */
	public static final class StopEvent extends Event {
		// EMPTY
	}

	/**
	 * The Activity visibility state changed.
	 */
	public static final class VisibilityEvent extends Event {

		/**
		 * The new visibility state of the Activity.
		 */
		public boolean isVisible = false;
	}

	/**
	 * Indicates the visibility state of this Activity.
	 */
	private boolean _isVisible = false;

	/**
	 * Indicates the visibility state of this Activity.
	 *
	 * @return true if the Activity is visible on screen, false otherwise.
	 */
	public boolean isVisible() {
		return _isVisible;
	}

	/**
	 * Overridden to provide Activity lifecycle Events to the EventBus.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);
		this.eventBus().publish(Event.build(CreateEvent.class, this)
				.assign("savedInstanceState", savedInstanceState)
				.assign("persistentState", persistentState)
				.create());
	}

	/**
	 * Overridden to provide Activity lifecycle Events to the EventBus.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		this.eventBus().publish(Event.build(StartEvent.class, this)
				.create());
	}

	/**
	 * Overridden to provide Activity lifecycle Events to the EventBus.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		_isVisible = true;
		this.eventBus().publish(Event.build(VisibilityEvent.class, this)
				.assign("isVisible", _isVisible)
				.create());
		this.eventBus().publish(Event.build(ResumeEvent.class, this)
				.create());
	}

	/**
	 * Overridden to provide Activity lifecycle Events to the EventBus.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		_isVisible = false;
		this.eventBus().publish(Event.build(VisibilityEvent.class, this)
				.assign("isVisible", _isVisible)
				.create());
		this.eventBus().publish(Event.build(PauseEvent.class, this)
				.create());
	}

	/**
	 * Overridden to provide Activity lifecycle Events to the EventBus.
	 */
	@Override
	protected void onStop() {
		super.onStop();
		this.eventBus().publish(Event.build(StopEvent.class, this)
				.create());
	}

	/**
	 * Overridden to provide Activity lifecycle Events to the EventBus.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		_isVisible = false;
		this.eventBus().publish(Event.build(VisibilityEvent.class, this)
				.assign("isVisible", _isVisible)
				.create());
		this.eventBus().publish(Event.build(DestroyEvent.class, this)
				.create());
	}

	/**
	 * Returns the application-wide instance of the EventBus.
	 *
	 * @implNote this method is equivalent to `this.getApplication().eventBus()`.
	 *
	 * @return the EventBus globally available to the application.
	 */
	public EventBus eventBus() {
		return ((BridgeApplication)this.getApplication()).eventBus();
	}
}
