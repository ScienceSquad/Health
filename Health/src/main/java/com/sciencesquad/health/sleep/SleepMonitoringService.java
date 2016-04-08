package com.sciencesquad.health.sleep;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.WakeLockManager;
import com.sciencesquad.health.core.util.Dispatcher;
import com.sciencesquad.health.core.util.Point;
import com.sciencesquad.health.core.util.X;

import java.io.*;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Math.*;

public class SleepMonitoringService extends Service implements SensorEventListener {
	private static final String TAG = SleepMonitoringService.class.getSimpleName();

	public final class SleepMonitoringBinder extends Binder {
		public SleepMonitoringService getService() {
			return SleepMonitoringService.this;
		}
	}

	public static final String KEY_ALARM_IN_SILENT_MODE = "alarm_in_silent_mode";
	public static final String KEY_ALARM_SNOOZE = "snooze_duration";
	public static final String KEY_VOLUME_BEHAVIOR = "volume_button_setting";
	public static final String PREFERENCES = "com.sciencesquad.health_preferences";
	public static final String EXTRA_ID = "id";
	public static final String EXTRA_NAME = "name";
	public static final String EXTRA_X = "x";
	public static final String EXTRA_Y = "y";
	public static final String EXTRA_AIRPLANE_MODE = "supportAirplaneMode";
	public static final String EXTRA_ALARM = "alarm";
	public static final String EXTRA_ALARM_WINDOW = "alarmWindow";
	public static final String EXTRA_FORCE_SCREEN_ON = "forceScreenOn";
	public static final String EXTRA_SENSOR_DELAY = "sensorDelay";
	public static final String EXTRA_SILENT_MODE = "supportSilentMode";
	public static final String EXTRA_USE_ALARM = "useAlarm";
	public static final String START_SLEEP = "START_SLEEP";
	public static final String SERVICE_IS_RUNNING = "serviceIsRunning";
	public static final String SLEEP_DATA = "sleepData";
	public static final String SLEEP_START = "SLEEP_START";
	public static final String SLEEP_STOPPED = "SLEEP_STOPPED";
	public static final String STOP_AND_SAVE_SLEEP = "STOP_AND_SAVE_SLEEP";
	public static final String EXTRA_IO_EXCEPTION = "IOException";
	public static final String EXTRA_NOTE = "note";
	public static final String EXTRA_RATING = "rating";
	public static final String EXTRA_URI = "uri";
	public static final String EXTRA_SUCCESS = "success";
	public static final String SAVE_SLEEP_COMPLETED = "SAVE_SLEEP_COMPLETED";
	public static final int MAX_POINTS_IN_A_GRAPH = 200;
	public static final float DEFAULT_ALARM_SENSITIVITY = 0.33F;
	public static final float DEFAULT_MIN_SENSITIVITY = 0.0F;
	public static final float MAX_ALARM_SENSITIVITY = 1.0F;
	public static final int INTERVAL = 5000;
	public static final int NOTIFICATION_ID = 0x1337a;
	public static final Object[] DATA_LOCK = new Object[0];

	private AtomicBoolean mRunning = new AtomicBoolean();
	private boolean supportAirplaneMode = false;
	private float alarmTriggerSensitivity = DEFAULT_ALARM_SENSITIVITY;
	private int alarmWindow = 30;
	final float alpha = 0.8f;
	private boolean alreadyDeletedResidualFile = false;
	private Date dateStarted = new Date();
	private boolean forceScreenOn = false;
	private final float[] gravity = { 0, 0, 0 };
	private double maxNetForce = DEFAULT_MIN_SENSITIVITY;
	private int _previousRingerMode = AudioManager.RINGER_MODE_NORMAL;
	public int sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
	private boolean supportSilentMode = false;
	private final ArrayList<Point> sleepData = new ArrayList<>();
	private int updateInterval = INTERVAL;
	Timer updateTimer = new Timer();
	private boolean useAlarm = false;
	int waitForSensorsToWarmUp = 0;
	private final BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();
			if (action.equals(STOP_AND_SAVE_SLEEP)) {
				// FIXME
				//final Intent saveIntent = addExtrasToSaveSleepIntent(new Intent(
				//		SleepMonitoringService.this, SaveSleepActivity.class));
				//startActivity(saveIntent);
				//sendBroadcast(new Intent(Alarms.CANCEL_SNOOZE));
				final long now = System.currentTimeMillis();
				/* FIXME
				try {
					final Alarm alarm = Alarms.calculateNextAlert(context);
					if (now > alarm.time + 60 * alarmWindow * 1000) {
						Alarms.setTimeToIgnore(context, alarm, alarm.time);
						Alarms.setNextAlert(context);
					}
				} catch (final NullPointerException npe) {
					// there are no enabled alarms
					Log.d(TAG, "No enabled alarms.");
				}
				*/
				createSaveSleepNotification();
				if (!mRunning.compareAndSet(true, false)) {
					Log.d(TAG, "Asked to stop and save sleep when not running.");
				}
				stopSelf();
			} else {
				/* FIXME
				if (action.equals(Alarms.CANCEL_SNOOZE)) {
					final long now = System.currentTimeMillis();
					try {
						final Alarm alarm = Alarms.getAlarm(context.getContentResolver(),
								intent.getIntExtra(Alarms.ALARM_ID, -1));
						if (now > alarm.time + 60 * alarmWindow * 1000) {
							Alarms.setTimeToIgnore(context, alarm, alarm.time);
							Alarms.setNextAlert(context);
						}
					} catch (final NullPointerException npe) {
						// there are no enabled alarms
						Log.d(TAG, "No enabled alarms.");
					}
				}
				*/
				createSaveSleepNotification();
				if (!mRunning.compareAndSet(true, false)) {
					Log.d(TAG, "Asked to cancel snooze when not running.");
				}
				stopSelf();
			}
		}
	};

	/**
	 * Convenience method to start the SoundService.
	 */
	public static void startMonitoringService() {
		X.of(BaseApp.app()).let(app -> {
			app.startService(new Intent(app, SleepMonitoringService.class));
		}).or(() -> {
			Log.d(TAG, "SoundService could not be started.");
		});
	}

	/**
	 * Convenience method to stop the SoundService.
	 * Note that this cannot be invoked by the user directly.
	 */
	public static void stopMonitoringService() {
		X.of(BaseApp.app()).let(app -> {
			app.stopService(new Intent(app, SleepMonitoringService.class));
		}).or(() -> {
			Log.d(TAG, "SoundService could not be stopped.");
		});
	}

	private void createSaveSleepNotification() {
		Intent intent = new Intent(this, SleepMonitoringService.class);
		intent.setAction("SAVE");
		PendingIntent pending = PendingIntent.getService(this, 0, intent, 0);
		Notification n = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_menu_manage)
				.setContentTitle(getText(R.string.notification_save_sleep_title))
				.setContentText(getText(R.string.notification_save_sleep_text))
				.setContentIntent(pending)
				.setDeleteIntent(pending)
				.setAutoCancel(true)
				.setOngoing(true)
				.build();

		NotificationManager m = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		m.notify(343, n);
	}

	private Notification createServiceNotification() {
		Intent intent = new Intent(this, SleepMonitoringService.class);
		intent.setAction("STOP");
		PendingIntent pending = PendingIntent.getService(this, 0, intent, 0);
		Notification n = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_menu_manage)
				.setContentTitle(getText(R.string.notification_sleep_title))
				.setContentText(getText(R.string.notification_sleep_text))
				.setContentIntent(pending)
				.setDeleteIntent(pending)
				.setAutoCancel(true)
				.setOngoing(true)
				.build();
		return n;
	}

	@Override
	public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
		// ignored
	}

	/**
	 * Handle that allows others to access the sleep monitoring
	 * service. An implementation of a binder that merely hands
	 * off the service its running within.
	 *
	 * @param intent
	 * @return
	 */
	@Override
	public IBinder onBind(final Intent intent) {
		return new SleepMonitoringBinder();
	}

	@Override
	public void onCreate() {
		final IntentFilter filter = new IntentFilter();
		filter.addAction("alarm_dismissed");
		filter.addAction("alarm_snooze_cancelled");
		filter.addAction(STOP_AND_SAVE_SLEEP);
		registerReceiver(serviceReceiver, filter);
	}

	@Override
	public void onDestroy() {
		((SensorManager)getSystemService(Context.SENSOR_SERVICE)).unregisterListener(this);

		WakeLockManager.release("sleepMonitoring");
		unregisterReceiver(serviceReceiver);
		sendBroadcast(new Intent(SLEEP_STOPPED));
		stopForeground(true);
		updateTimer.cancel();

		Dispatcher.UTILITY.run(() -> {
			toggleSilentMode(false);
			toggleAirplaneMode(false);
			getSharedPreferences(SERVICE_IS_RUNNING, Context.MODE_PRIVATE)
					.edit()
					.putBoolean(SERVICE_IS_RUNNING, false)
					.apply();
		});
	}

	@Override
	public void onSensorChanged(final SensorEvent event) {
		new Thread(() -> { synchronized (gravity) {
			if (waitForSensorsToWarmUp < 5) {
				if (waitForSensorsToWarmUp == 4) {
					waitForSensorsToWarmUp++;
					try {
						updateTimer.scheduleAtFixedRate(
								new TimerTask() {
									@Override
									public void run() {
										final long currentTime = System.currentTimeMillis();

										final long x = currentTime;
										final double y = min(MAX_ALARM_SENSITIVITY, maxNetForce);

										final Point sleepPoint = new Point((double) currentTime, y);
										if (sleepData.size() >= MAX_POINTS_IN_A_GRAPH) {
											sleepData.remove(0);
										}
										sleepData.add(sleepPoint);

										// append the two doubles in sleepPoint to file
										try { synchronized (DATA_LOCK) {
											final FileOutputStream fos = openFileOutput(SLEEP_DATA, Context.MODE_APPEND);
											fos.write(Point.toByteArray(sleepPoint));
											fos.close();
										}} catch (Exception ignored) {}

										final Intent i = null;//new Intent(SleepActivity.UPDATE_CHART);
										//i.putExtra(EXTRA_X, x);
										//i.putExtra(EXTRA_Y, y);

										//TODO: ensure that this isn't needed anymore.
										//i.putExtra(SleepStartReceiver.EXTRA_ALARM, alarmTriggerSensitivity);
										//sendBroadcast(i);
										Log.i(TAG, "Update " + x + " | " + y);

										maxNetForce = 0;

										triggerAlarm(currentTime, y);
									}
								},
								updateInterval, updateInterval);
					} catch (IllegalStateException ise) {
						// user stopped monitoring really quickly after
						// starting.
						Log.d(TAG, "User stopped monitoring quickly after starting.");
					}
					gravity[0] = event.values[0];
					gravity[1] = event.values[1];
					gravity[2] = event.values[2];
				}
				waitForSensorsToWarmUp++;
				return;
			}

			gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
			gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
			gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

			final double curX = event.values[0] - gravity[0];
			final double curY = event.values[1] - gravity[1];
			final double curZ = event.values[2] - gravity[2];

			final double mAccelCurrent = sqrt(curX * curX + curY * curY + curZ * curZ);

			final double absAccel = abs(mAccelCurrent);
			maxNetForce = absAccel > maxNetForce ? absAccel : maxNetForce;
		}}).start();
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		if (intent == null || !mRunning.compareAndSet(false, true))
			return START_STICKY;
		Log.i(TAG, "Started monitoring service...");

		// Configure the monitoring service.
		int testModeRate = intent.getIntExtra("testModeRate", Integer.MIN_VALUE);
		updateInterval = testModeRate == Integer.MIN_VALUE ? intent.getIntExtra("interval", INTERVAL) : testModeRate;
		sensorDelay = intent.getIntExtra(EXTRA_SENSOR_DELAY, SensorManager.SENSOR_DELAY_FASTEST);
		alarmTriggerSensitivity = intent.getFloatExtra(EXTRA_ALARM, DEFAULT_ALARM_SENSITIVITY);
		useAlarm = intent.getBooleanExtra(EXTRA_USE_ALARM, false);
		alarmWindow = intent.getIntExtra(EXTRA_ALARM_WINDOW, 0);
		supportAirplaneMode = intent.getBooleanExtra(EXTRA_AIRPLANE_MODE, false);
		supportSilentMode = intent.getBooleanExtra(EXTRA_SILENT_MODE, false);
		forceScreenOn = intent.getBooleanExtra(EXTRA_FORCE_SCREEN_ON, false);

		// Start a Service notification.
		startForeground(NOTIFICATION_ID, createServiceNotification());

		// Grab a WakeLock
		final int wakeLockType = forceScreenOn ? (PowerManager.SCREEN_DIM_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE | PowerManager.ACQUIRE_CAUSES_WAKEUP)
				: PowerManager.PARTIAL_WAKE_LOCK;
		WakeLockManager.acquire(this, "sleepMonitoring", wakeLockType);

		Log.i(TAG, "Wakelock acquired!");

		// Register SensorListener
		final SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorDelay);

		Log.i(TAG, "Sensors established.");

		// Prepare for start in the background.
		Dispatcher.UTILITY.run(() -> {
			toggleSilentMode(true);
			toggleAirplaneMode(true);
			if (!alreadyDeletedResidualFile) {
				// TODO: doesn't happen more than once? right?
				deleteFile(SleepMonitoringService.SLEEP_DATA);
				alreadyDeletedResidualFile = true;
			}
			getSharedPreferences(SERVICE_IS_RUNNING, Context.MODE_PRIVATE)
					.edit()
					.putBoolean(SERVICE_IS_RUNNING, true)
					.apply();

			Log.i(TAG, "Configuration done!");
		});
		return START_STICKY;
	}

	/**
	 * Toggle the Airplane mode if we are configured to do that.
	 *
	 * @param enable turn on or off the Airplane mode
	 */
	private void toggleAirplaneMode(final boolean enable) {
		if (!supportAirplaneMode) return;

		Settings.Global.putInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, enable ? 1 : 0);
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", enable);
		sendBroadcast(intent);
	}

	/**
	 * Toggle the Ringer mode if we are configured to do that.
	 *
	 * @param enable turn on or off the Ringer mode
	 */
	private void toggleSilentMode(final boolean enable) {
		if (!supportSilentMode) return;

		AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
		if (enable) {
			_previousRingerMode = audioManager.getRingerMode();
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		} else audioManager.setRingerMode(_previousRingerMode);
	}

	/**
	 * Trigger an alarm if necessary.
	 *
	 * @param time
	 * @param sensitivity
	 */
	private void triggerAlarm(final long time, final double sensitivity) {
		if (!useAlarm || sensitivity < alarmTriggerSensitivity)
			return;

		// FIXME: stop calling calculateNextAlert here... battery waster
		//final Alarm alarm = Alarms.calculateNextAlert(this);
		if (false) return;//alarm != null)

		final Calendar alarmTime = Calendar.getInstance();
		//alarmTime.setTimeInMillis(alarm.time);
		alarmTime.add(Calendar.MINUTE, alarmWindow * -1);
		final long alarmMillis = alarmTime.getTimeInMillis();

		if (time >= alarmMillis) {
			final SharedPreferences alarmPrefs = getSharedPreferences(PREFERENCES, 0);
			final int id = 0;//alarmPrefs.getInt(Alarms.PREF_SNOOZE_ID, -1);
			// if not already snoozing off ANY alarm, trigger the alarm
			if (id == -1) {
				// add 1 second delay to make it less likely that we skip the alarm FIXME
				//Alarms.enableAlert(this, alarm, System.currentTimeMillis() + 1000);
			}
		}
	}

	private void startSleeping() {
		Context context = this; // FIXME
		final SharedPreferences userPrefs = context.getSharedPreferences(
				SleepMonitoringService.PREFERENCES, 0);
		final float alarmTriggerSensitivity = userPrefs.getFloat(
				context.getString(R.string.pref_alarm_trigger_sensitivity),
				SleepMonitoringService.DEFAULT_ALARM_SENSITIVITY);
		final int sensorDelay = Integer.parseInt(userPrefs.getString(
				context.getString(R.string.pref_sensor_delay), ""
						+ SensorManager.SENSOR_DELAY_NORMAL));
		final boolean useAlarm = userPrefs.getBoolean(
				context.getString(R.string.pref_use_alarm), false);
		final int alarmWindow = Integer.parseInt(userPrefs.getString(
				context.getString(R.string.pref_alarm_window), "-1"));
		final boolean airplaneMode = userPrefs.getBoolean(
				context.getString(R.string.pref_airplane_mode), false);
		final boolean silentMode = userPrefs.getBoolean(
				context.getString(R.string.pref_silent_mode), false);
		final boolean forceScreenOn = userPrefs.getBoolean(
				context.getString(R.string.pref_force_screen), false);

		Intent serviceIntent = new Intent(context, SleepMonitoringService.class);
		serviceIntent.putExtra(SleepMonitoringService.EXTRA_ALARM, alarmTriggerSensitivity);
		serviceIntent.putExtra(SleepMonitoringService.EXTRA_SENSOR_DELAY, sensorDelay);
		serviceIntent.putExtra(SleepMonitoringService.EXTRA_USE_ALARM, useAlarm);
		serviceIntent.putExtra(SleepMonitoringService.EXTRA_ALARM_WINDOW, alarmWindow);
		serviceIntent.putExtra(SleepMonitoringService.EXTRA_AIRPLANE_MODE, airplaneMode);
		serviceIntent.putExtra(SleepMonitoringService.EXTRA_SILENT_MODE, silentMode);
		serviceIntent.putExtra(SleepMonitoringService.EXTRA_FORCE_SCREEN_ON, forceScreenOn);

	}

	private void finishSleep(Intent intent) {
		Context context = this; // FIXME
		final double alarm = 0.0;//intent.getDoubleExtra(SleepStartReceiver.EXTRA_ALARM, SettingsActivity.DEFAULT_ALARM_SENSITIVITY);

		final String name = intent.getStringExtra(SleepMonitoringService.EXTRA_NAME);
		final int rating = intent.getIntExtra(EXTRA_RATING, 5);
		final String note = intent.getStringExtra(EXTRA_NOTE);

		FileInputStream fis;
		// RandomAccessFile raFile;
		List<Point> originalData = null;
		try {
			final File dataFile = context
					.getFileStreamPath(SleepMonitoringService.SLEEP_DATA);
			// raFile = new RandomAccessFile(dataFile, "r");
			fis = context.openFileInput(SleepMonitoringService.SLEEP_DATA);
			final long length = dataFile.length();
			final int chunkSize = 16;
			if (length % chunkSize != 0) {
				context.sendBroadcast(new Intent(SAVE_SLEEP_COMPLETED).putExtra(
						EXTRA_IO_EXCEPTION, "corrupt file"));
				return;
			}
			originalData = new ArrayList<Point>((int) (length / chunkSize / 2));
			if (length >= chunkSize) {
				final byte[] wholeFile = new byte[(int) length];
				final byte[] buffer = new byte[8192];
				int bytesRead = 0;
				int dstPos = 0;
				while ((bytesRead = fis.read(buffer)) != -1) {
					System.arraycopy(buffer, 0, wholeFile, dstPos, bytesRead);
					dstPos += bytesRead;
				}
				fis.close();
				final byte[] chunk = new byte[chunkSize];
				for (int i = 0; i < wholeFile.length; i += chunkSize) {
					System.arraycopy(wholeFile, i, chunk, 0, chunkSize);
					originalData.add(Point.fromByteArray(chunk));
				}
			}
		} catch (final FileNotFoundException e) {
			context.sendBroadcast(new Intent(SAVE_SLEEP_COMPLETED).putExtra(
					EXTRA_IO_EXCEPTION, e.getMessage()));
			return;
		} catch (final IOException e) {
			context.sendBroadcast(new Intent(SAVE_SLEEP_COMPLETED).putExtra(
					EXTRA_IO_EXCEPTION, e.getMessage()));
			return;
		}

		context.deleteFile(SleepMonitoringService.SLEEP_DATA);

		final int numberOfPointsOriginal = originalData.size();

		// List<Double> mX = (List<Double>) intent
		// .getSerializableExtra("currentSeriesX");
		// List<Double> mY = (List<Double>) intent
		// .getSerializableExtra("currentSeriesY");

		if (numberOfPointsOriginal == 0) {
			context.sendBroadcast(new Intent(SAVE_SLEEP_COMPLETED));
			return;
		}

		final int numberOfDesiredGroupedPoints = SleepMonitoringService.MAX_POINTS_IN_A_GRAPH;
		// numberOfDesiredGroupedPoints = numberOfPointsOriginal >
		// numberOfDesiredGroupedPoints ? numberOfDesiredGroupedPoints
		// : numberOfPointsOriginal;
		Uri createdUri = null;
		if (numberOfDesiredGroupedPoints <= numberOfPointsOriginal) {
			final int pointsPerGroup = numberOfPointsOriginal
					/ numberOfDesiredGroupedPoints + 1;
			final List<Point> lessDetailedData = new ArrayList<>(
					numberOfDesiredGroupedPoints);
			int numberOfPointsInThisGroup = pointsPerGroup;
			double maxYForThisGroup;
			double totalForThisGroup;
			int numberOfSpikes = 0;
			int numberOfConsecutiveNonSpikes = 0;
			long timeOfFirstSleep = 0;
			for (int i = 0; i < numberOfDesiredGroupedPoints; i++) {
				maxYForThisGroup = 0;
				totalForThisGroup = 0;
				final int startIndexForThisGroup = i * pointsPerGroup;
				for (int j = 0; j < pointsPerGroup; j++) {
					try {
						final double currentY = originalData
								.get(startIndexForThisGroup + j).y;
						if (currentY > maxYForThisGroup) {
							maxYForThisGroup = currentY;
						}
						totalForThisGroup += currentY;
					} catch (final IndexOutOfBoundsException ioobe) {
						// lower the number of points
						// (and thereby signify that we are done)
						numberOfPointsInThisGroup = j - 1;
						break;
					}
				}
				final double averageForThisGroup = totalForThisGroup
						/ numberOfPointsInThisGroup;
				if (numberOfPointsInThisGroup < pointsPerGroup) {
					// we are done
					final int lastIndex = numberOfPointsOriginal - 1;
					lessDetailedData.add(originalData.get(lastIndex));
					break;
				} else {
					if (maxYForThisGroup < alarm) {
						maxYForThisGroup = averageForThisGroup;
						if (timeOfFirstSleep == 0 && ++numberOfConsecutiveNonSpikes > 4) {
							final int lastIndex = lessDetailedData.size() - 1;

							timeOfFirstSleep = Math.round(lessDetailedData.get(lastIndex).x);
						}
					} else {
						numberOfConsecutiveNonSpikes = 0;
						numberOfSpikes++;
					}
					lessDetailedData.add(new Point(originalData
							.get(startIndexForThisGroup).x, maxYForThisGroup));
				}
			}

			final long endTime = Math.round(lessDetailedData.get(lessDetailedData.size() - 1).x);
			final long startTime = Math.round(lessDetailedData.get(0).x);

			//final SleepSession session = new SleepSession(name, lessDetailedData,
			//		SettingsActivity.DEFAULT_MIN_SENSITIVITY, alarm, rating, endTime
			//		- startTime, numberOfSpikes, timeOfFirstSleep, note);
			//createdUri = SleepSessions.createSession(context, session);
		} else {

			final long endTime = Math.round(originalData.get(numberOfPointsOriginal - 1).x);
			final long startTime = Math.round(originalData.get(0).x);

			int numberOfSpikes = 0;
			int numberOfConsecutiveNonSpikes = 0;
			long timeOfFirstSleep = endTime;
			for (int i = 0; i < numberOfPointsOriginal; i++) {
				final double currentY = originalData.get(i).y;
				if (currentY < alarm) {
					if (timeOfFirstSleep == endTime && ++numberOfConsecutiveNonSpikes > 4) {
						final int lastIndex = originalData.size() - 1;

						timeOfFirstSleep = Math.round(originalData.get(lastIndex).x);
					}
				} else {
					numberOfConsecutiveNonSpikes = 0;
					numberOfSpikes++;
				}
			}
			//final SleepSession session = new SleepSession(name, originalData,
			//		SettingsActivity.DEFAULT_MIN_SENSITIVITY, alarm, rating, endTime
			//		- startTime, numberOfSpikes, timeOfFirstSleep, note);
			//createdUri = SleepSessions.createSession(context, session);
		}

		final Intent saveSleepCompletedIntent = new Intent(SAVE_SLEEP_COMPLETED);
		saveSleepCompletedIntent.putExtra(EXTRA_SUCCESS, true);
		saveSleepCompletedIntent.putExtra(EXTRA_URI, createdUri.toString());
		context.sendBroadcast(saveSleepCompletedIntent);
	}

	private Intent addExtrasToSaveSleepIntent(final Intent saveIntent) {
		saveIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
		saveIntent.putExtra(EXTRA_ID, hashCode());
		saveIntent.putExtra(EXTRA_ALARM, alarmTriggerSensitivity);

		// send start/end time as well
		final DateFormat sdf = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
				Locale.getDefault());
		DateFormat sdf2 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
				Locale.getDefault());
		final Date now = new Date();
		if (dateStarted.getDate() == now.getDate()) {
			sdf2 = DateFormat.getTimeInstance(DateFormat.SHORT);
		}
		saveIntent.putExtra(EXTRA_NAME, sdf.format(dateStarted) + " " + getText(R.string.to) + " "
				+ sdf2.format(now));
		return saveIntent;
	}
}