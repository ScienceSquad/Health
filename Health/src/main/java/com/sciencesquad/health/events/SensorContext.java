package com.sciencesquad.health.events;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import com.sciencesquad.health.BaseApplication;
import java8.util.Optional;
import org.immutables.value.Value.Immutable;

import java.util.HashMap;

import static com.sciencesquad.health.events.Event.EventType;

/**
 *
 */
public class SensorContext {

	/**
	 *
	 */
	public class SensorNotAvailableException extends Exception {
		public SensorNotAvailableException(String message) {
			super(message);
		}
 	}

	/**
	 *
	 */
	@Immutable @EventType
	public interface SensorChange extends Event {

		/**
		 *
		 * @return
		 */
		SensorEvent sensorEvent();
	}

	/**
	 *
	 */
	@Immutable @EventType
	public interface SensorAccuracy extends Event {

		/**
		 *
		 * @return
		 */
		Sensor sensor();

		/**
		 *
		 * @return
		 */
		int accuracy();
	}

	/**
	 *
	 */
	@Immutable @EventType
	public interface SensorFlush extends Event {

		/**
		 *
		 * @return
		 */
		Sensor sensor();
	}

	private SensorManager _sensorManager;
	private HashMap<Integer, SensorEventListener2> _sensorListeners = new HashMap<>();

	/**
	 *
	 * @param context
	 */
	public SensorContext(final Context context) {
		this._sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
	}

	/**
	 *
	 * @param sensorType
	 * @param samplePeriod
	 * @throws SensorNotAvailableException
	 */
	public void requestSensorEvents(int sensorType, int samplePeriod) throws SensorNotAvailableException {
		final Sensor sensor = this._sensorManager.getDefaultSensor(sensorType);
		if (sensor == null || this._sensorListeners.containsKey(sensorType)) {
			throw new SensorNotAvailableException("Sensor " + sensorType + " is not available on this device!");
		}

		final SensorEventListener2 listener = new SensorEventListener2() {
			@Override public void onSensorChanged(SensorEvent sensorEvent) {
				Optional.ofNullable(BaseApplication.application())
						.map(BaseApplication::eventBus)
						.ifPresent(e -> {
							e.publish(SensorChangeEvent.from(this)
									.sensorEvent(sensorEvent)
									.create());
						});
			}

			@Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
				Optional.ofNullable(BaseApplication.application())
						.map(BaseApplication::eventBus)
						.ifPresent(e -> {
							e.publish(SensorAccuracyEvent.from(this)
									.sensor(sensor)
									.accuracy(accuracy)
									.create());
						});
			}

			@Override public void onFlushCompleted(Sensor sensor) {
				Optional.ofNullable(BaseApplication.application())
						.map(BaseApplication::eventBus)
						.ifPresent(e -> {
							e.publish(SensorFlushEvent.from(this)
									.sensor(sensor)
									.create());
						});
			}
		};

		this._sensorListeners.put(sensorType, listener);
		this._sensorManager.registerListener(listener, sensor, samplePeriod);
	}

	/**
	 *
	 * @param sensorType
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
