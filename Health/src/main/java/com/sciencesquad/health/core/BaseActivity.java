package com.sciencesquad.health.core;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import com.sciencesquad.health.core.Event.EventType;
import org.immutables.value.Value.Immutable;

public class BaseActivity extends AppCompatActivity {
	private static final String TAG = BaseActivity.class.getSimpleName();

	/**
	 * The Activity was created.
	 */
	@Immutable @EventType
	public interface ActivityCreate extends Event {

		/**
		 * The saved instance state of this Activity.
		 */
		// FIXME: Probably should not be broadcasting this...
		Bundle savedInstanceState();

		/**
		 * The persistent state of this Activity.
		 */
		// FIXME: Probably should not be broadcasting this...
		PersistableBundle persistentState();
	}

	/**
	 * The Activity was destroyed.
	 */
	@Immutable @EventType
	public interface ActivityDestroy extends Event {
		// EMPTY
	}

	/**
	 * The Activity was paused.
	 */
	@Immutable @EventType
	public interface ActivityPause extends Event {
		// EMPTY
	}

	/**
	 * The Activity was resumed.
	 */
	@Immutable @EventType
	public interface ActivityResume extends Event {
		// EMPTY
	}

	/**
	 * The Activity was started.
	 */
	@Immutable @EventType
	public interface ActivityStart extends Event {
		// EMPTY
	}

	/**
	 * The Activity was stopped.
	 */
	@Immutable @EventType
	public interface ActivityStop extends Event {
		// EMPTY
	}

	/**
	 * The Activity visibility state changed.
	 */
	@Immutable @EventType
	public interface ActivityVisibility extends Event {

		/**
		 * The new visibility state of the Activity.
		 */
		boolean visible();
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
		this.eventBus().publish(ActivityCreateEvent.from(this)
				.savedInstanceState(savedInstanceState)
				.persistentState(persistentState)
				.create());
	}

	/**
	 * Overridden to provide Activity lifecycle Events to the EventBus.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		this.eventBus().publish(ActivityStartEvent.from(this)
				.create());
	}

	/**
	 * Overridden to provide Activity lifecycle Events to the EventBus.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		_isVisible = true;
		this.eventBus().publish(ActivityVisibilityEvent.from(this)
				.visible(_isVisible)
				.create());
		this.eventBus().publish(ActivityResumeEvent.from(this)
				.create());
	}

	/**
	 * Overridden to provide Activity lifecycle Events to the EventBus.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		_isVisible = false;
		this.eventBus().publish(ActivityVisibilityEvent.from(this)
				.visible(_isVisible)
				.create());
		this.eventBus().publish(ActivityPauseEvent.from(this)
				.create());
	}

	/**
	 * Overridden to provide Activity lifecycle Events to the EventBus.
	 */
	@Override
	protected void onStop() {
		super.onStop();
		this.eventBus().publish(ActivityStopEvent.from(this)
				.create());
	}

	/**
	 * Overridden to provide Activity lifecycle Events to the EventBus.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		_isVisible = false;
		this.eventBus().publish(ActivityVisibilityEvent.from(this)
				.visible(_isVisible)
				.create());
		this.eventBus().publish(ActivityDestroyEvent.from(this)
				.create());
	}

	/**
	 * Returns the app-wide instance of the EventBus.
	 *
	 * @implNote this method is equivalent to `this.getApplication().eventBus()`.
	 *
	 * @return the EventBus globally available to the app.
	 */
	public EventBus eventBus() {
		return ((BaseApp)this.getApplication()).eventBus();
	}
}
