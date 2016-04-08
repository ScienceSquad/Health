package com.sciencesquad.health.core.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import java8.util.function.Consumer;

public final class SoundMeter {

	private int minSize = AudioRecord.getMinBufferSize(
			8000, AudioFormat.CHANNEL_IN_MONO,
			AudioFormat.ENCODING_PCM_16BIT);
	private AudioRecord ar = new AudioRecord(
			MediaRecorder.AudioSource.MIC,
			8000, AudioFormat.CHANNEL_IN_MONO,
			AudioFormat.ENCODING_PCM_16BIT, minSize);

	public SoundMeter start() {
		if (ar != null)
			ar.startRecording();
		return this;
	}

	public SoundMeter stop() {
		if (ar != null)
			ar.stop();
		return this;
	}

	public double getAmplitude() {
		short[] buffer = new short[minSize];
		ar.read(buffer, 0, minSize);

		int max = 0;
		for (short s : buffer)
			if (Math.abs(s) > max)
				max = Math.abs(s);
		return max;
	}

	public SoundMeter withAmplitude(Consumer<Double> handler) {
		handler.accept(getAmplitude());
		return this;
	}
}