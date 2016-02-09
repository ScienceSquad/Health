package com.sciencesquad.health.health;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

public class BridgeActivity extends AppCompatActivity {
	private static final String TAG = "BridgeActivity";

	public static final class CreateEvent extends Event {
		public Bundle savedInstanceState; // FIXME: Probably should not be broadcasting this...
		public PersistableBundle persistentState; // FIXME: Probably should not be broadcasting this...
	}

	public static final class DestroyEvent extends Event {
		// EMPTY
	}

	public static final class PauseEvent extends Event {
		// EMPTY
	}

	public static final class ResumeEvent extends Event {
		// EMPTY
	}

	public static final class StartEvent extends Event {
		// EMPTY
	}

	public static final class StopEvent extends Event {
		// EMPTY
	}

	public static final class VisibilityEvent extends Event {
		public boolean isVisible = false;
	}

	private boolean _isVisible = false;

	public boolean isVisible() {
		return _isVisible;
	}

	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);
		this.eventBus().publish(Event.build(CreateEvent.class, this)
				.assign("savedInstanceState", savedInstanceState)
				.assign("persistentState", persistentState)
				.create());
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.eventBus().publish(Event.build(StartEvent.class, this)
				.create());
	}

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

	@Override
	protected void onStop() {
		super.onStop();
		this.eventBus().publish(Event.build(StopEvent.class, this)
				.create());
	}

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

	// Helper
	public EventBus eventBus() {
		return ((BridgeApplication)this.getApplication()).eventBus();
	}

	// Helper
	public void runOnUiThreadDelayed(Runnable action, long delay) {
		new Handler(getMainLooper()).postDelayed(action, delay);
	}
}
