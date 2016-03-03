package com.sciencesquad.health.ui;


import android.util.Log;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.temporal.TemporalUnit;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by andrew on 3/3/16.
 */
public class Alarm {

	private Calendar time;

	public Alarm() {
		this.time = Calendar.getInstance();
		Log.d("Alarm.java", "Year : " + String.valueOf(this.time.get(Calendar.YEAR)));
	}

	public int get(int field) {
		return this.time.get(field);
	}

	public void set(int field, int value) {
		this.time.set(field, value);
	}

	public String getFieldString(int field, boolean prettyName) {
		String value = String.valueOf(this.get(field));
		if (prettyName) {
			if (field == Calendar.MONTH) {
				value = this.time.getDisplayName(field, Calendar.LONG, Locale.US);
			}
		}
		return value;
	}

}
