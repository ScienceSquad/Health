package com.sciencesquad.health.sleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.sciencesquad.health.core.util.Point;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SleepEndReceiver extends BroadcastReceiver {

	public static final String EXTRA_IO_EXCEPTION = "IOException";
	public static final String EXTRA_NOTE = "note";
	public static final String EXTRA_RATING = "rating";
	public static final String EXTRA_URI = "uri";
	public static final String EXTRA_SUCCESS = "success";
	public static final String SAVE_SLEEP_COMPLETED = "SAVE_SLEEP_COMPLETED";

	@Override
	public void onReceive(final Context context, final Intent intent) {
		new Thread(() -> {
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
		}).start();
	}
}