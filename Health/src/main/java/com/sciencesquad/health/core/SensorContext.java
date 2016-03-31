package com.sciencesquad.health.core;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import com.sciencesquad.health.events.SensorAccuracyEvent;
import com.sciencesquad.health.events.SensorChangeEvent;
import com.sciencesquad.health.events.SensorFlushEvent;
import java8.util.Optional;
import org.immutables.value.Value.Immutable;

import java.util.HashMap;

import static com.sciencesquad.health.core.Event.EventType;

/**
 * The SensorContext is a transparent entity bound to the app
 * which can be requested to translate callback functions into events.
 * A typical use-case is that of the normal `SensorManager` -- instead,
 * the request is made to the `SensorContext`, and that sends events
 * to all listeners, instead of just one. These events can then be
 * filtered to match what is required of the client.
 *
 * TODO: Events must manually be paused when there is no on-screen activity.
 * TODO: Can probably be reimplemented in a Service container.
 */
public class SensorContext {

	/**
	 * An exception thrown if the Sensor is not available when requested.
	 */
	public class SensorNotAvailableException extends Exception {
		public SensorNotAvailableException(String message) {
			super(message);
		}
 	}

	/**
	 * The sensor specified by sensorEvent has changed value.
	 */
	@Immutable @EventType
	public interface SensorChange extends Event {

		/**
		 * The underlying SensorEvent.
		 */
		SensorEvent sensorEvent();
	}

	/**
	 * The sensor specified by sensor has changed accuracy.
	 */
	@Immutable @EventType
	public interface SensorAccuracy extends Event {

		/**
		 * The underlying Sensor.
		 */
		Sensor sensor();

		/**
		 * The new accuracy level.
		 */
		int accuracy();
	}

	/**
	 * The sensor specified by sensor flushed its data.
	 */
	@Immutable @EventType
	public interface SensorFlush extends Event {

		/**
		 * The underlying Sensor.
		 */
		Sensor sensor();
	}

	/**
	 * The underlying SensorManager.
	 */
	private SensorManager _sensorManager;

	/**
	 * The set of all `SensorListerner2`s which broadcast events.
	 */
	private HashMap<Integer, SensorEventListener2> _sensorListeners = new HashMap<>();

	/**
	 * Initializes the SensorContext.
	 *
	 * @param context the context to initialize SensorContext with
	 */
	public SensorContext(final Context context) {
		this._sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
	}

	/**
	 * Returns the Sensor for the given sensorType.
	 *
	 * @param sensorType the sensor as an int
	 * @return the sensor as a Sensor
	 */
	@Nullable
	public Sensor sensorForType(int sensorType) {
		return this._sensorManager.getDefaultSensor(sensorType);
	}

	/**
	 * Requests sensor events for the given sensorType with the
	 * added sample period and latency modifiers.
	 *
	 * @param sensorType
	 * @param samplePeriod
	 * @throws SensorNotAvailableException if the sensor is unavailable
	 */
	public void requestSensorEvents(int sensorType, int samplePeriod, int maxLatency) throws SensorNotAvailableException {
		final Sensor sensor = this.sensorForType(sensorType);

		// Ensure the sensor is available, and is not a trigger-mode event.
		if (sensor == null)
			throw new SensorNotAvailableException("Sensor " + sensorType + " is not available on this device!");
		if (sensorType == Sensor.TYPE_SIGNIFICANT_MOTION)
			throw new SensorNotAvailableException("Significant Motion Triggers not supported!");

		// Pass-through case, since multiple requests will be automatically satisfied.
		if (this._sensorListeners.containsKey(sensorType))
			return;

		final SensorEventListener2 listener = new SensorEventListener2() {
			@Override public void onSensorChanged(SensorEvent sensorEvent) {
				Optional.ofNullable(BaseApp.app())
						.map(BaseApp::eventBus)
						.ifPresent(e -> {
							e.publish(SensorChangeEvent.from(this)
									.sensorEvent(sensorEvent)
									.create());
						});
			}

			@Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
				Optional.ofNullable(BaseApp.app())
						.map(BaseApp::eventBus)
						.ifPresent(e -> {
							e.publish(SensorAccuracyEvent.from(this)
									.sensor(sensor)
									.accuracy(accuracy)
									.create());
						});
			}

			@Override public void onFlushCompleted(Sensor sensor) {
				Optional.ofNullable(BaseApp.app())
						.map(BaseApp::eventBus)
						.ifPresent(e -> {
							e.publish(SensorFlushEvent.from(this)
									.sensor(sensor)
									.create());
						});
			}
		};

		this._sensorListeners.put(sensorType, listener);
		this._sensorManager.registerListener(listener, sensor, samplePeriod, maxLatency);
	}

	/**
	 * Relinquishes the sensor events requested for this sensor type.
	 * @param sensorType the sensor type
	 */
	public void relinquishSensorEvents(int sensorType) {
		Optional.ofNullable(this._sensorListeners.get(sensorType))
				.ifPresent(l -> {
					Runnable action = () -> this._sensorManager.unregisterListener(l);

					// Ensure execution on the main thread if not already there.
					if (Looper.getMainLooper() != Looper.myLooper())
						new Handler(Looper.getMainLooper()).post(action);
					else action.run();
				});
	}
}
