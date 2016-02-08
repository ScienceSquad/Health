package com.sciencesquad.health.health;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

public class BridgeActivity extends AppCompatActivity {

	private static final String TAG = "BridgeActivity";

	public interface ActivityEvent extends EventBus.Event {
		// intentionally empty
	}

	public final class ActivityCreateEvent implements ActivityEvent {

	}

	public final class ActivityDestroyEvent implements ActivityEvent {

	}

	public final class ActivityPauseEvent implements ActivityEvent {

	}

	public final class ActivityResumeEvent implements ActivityEvent {

	}

	public final class ActivityStartEvent implements ActivityEvent {

	}

	public final class ActivityStopEvent implements ActivityEvent {

	}

	public final class ActivityVisibilityEvent implements ActivityEvent {
		private boolean isVisible = false;
		public ActivityVisibilityEvent(boolean isVisible) {
			this.isVisible = isVisible;
		}
	}

	private boolean _isVisible = false;

	public boolean isVisible() {
		return _isVisible;
	}

	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);
		this.eventBus().publish(new ActivityCreateEvent());
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.eventBus().publish(new ActivityStartEvent());
	}

	@Override
	protected void onResume() {
		super.onResume();
		_isVisible = true;
		this.eventBus().publish(new ActivityVisibilityEvent(_isVisible));
		this.eventBus().publish(new ActivityResumeEvent());
	}

	@Override
	protected void onPause() {
		super.onPause();
		_isVisible = false;
		this.eventBus().publish(new ActivityVisibilityEvent(_isVisible));
		this.eventBus().publish(new ActivityPauseEvent());
	}

	@Override
	protected void onStop() {
		super.onStop();
		this.eventBus().publish(new ActivityStopEvent());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		_isVisible = false;
		this.eventBus().publish(new ActivityVisibilityEvent(_isVisible));
		this.eventBus().publish(new ActivityDestroyEvent());
	}

	// Helper
	public EventBus eventBus() {
		return ((BridgeApplication)this.getApplication()).eventBus();
	}

	// Helper
	public void runOnUiThreadDelayed(Runnable action, long delay) {
		new Handler(getMainLooper()).postDelayed(action, delay);
	}
}
