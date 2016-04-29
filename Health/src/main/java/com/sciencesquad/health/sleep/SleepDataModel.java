package com.sciencesquad.health.sleep;

import io.realm.RealmObject;

import java.util.Date;

/**
 * The model that records the user's sleep through a period of time,
 * along with data, as a session. This data is in accordance with the
 * Pittsburgh Sleep Quality Index (PSQI) and can be used for such
 * calculation or pattern learning.
 */
public class SleepDataModel extends RealmObject {

	/**
	 * The DreamType can be one of:
	 * 	- UNKNOWN: no user report provided
	 * 	- DREAM: the user had a pleasant dream
	 * 	- NIGHTMARE: the user had an upsetting dream
	 */
	enum DreamType {
		UNKNOWN, DREAM, NIGHTMARE
	}

	/**
	 * The DiscomfortType can be one of:
	 * 	- UNKNOWN: no user report provided
	 * 	- NONE: no general discomfort, possibly comfort
	 * 	- COLD: the user felt cold during this session
	 * 	- HOT: the user felt hot during this session
	 * 	- PAIN: the user was in pain during this session
	 */
	enum DiscomfortType {
		UNKNOWN, NONE, COLD, HOT, PAIN
	}

	/**
	 * The date this session occurred.
	 */
	public Date date; // LocalDate

	/**
	 * When the sleep interval began.
	 */
	public Date start; // LocalTime

	/**
	 * When the sleep interval ended.
	 */
	public Date end; // LocalTime

	/**
	 * The time taken between beginning the session and actually entering
	 * deep sleep (usually marked as REM sleep).
	 */
	public long untilDeepSleep; // Duration

	/**
	 * The user-reported type of dream that occurred during this session.
	 * (For either sleep or a short nap.)
	 */
	public int dreamType; //DreamType

	/**
	 * The user-reported dream diary for this session.
	 * This should only be for the user to review; not for number crunching.
	 */
	public String dreamDiary;

	/**
	 * The user-reported type of discomfort (or comfort) that occurred
	 * during this session.
	 *
	 * If the user reports something like "do not remember," use
	 * ComfortType.UNKNOWN. If they report "comfortable" or "nothing
	 * in particular," use ComfortType.NONE.
	 */
	public int discomfortType; // DiscomfortType

	/**
	 * A list of all (time, duration) tuples that correspond to points
	 * during this session of sleep, where a noise above the ambient threshold
	 * was detected and confirmed by the user as a "sleep noise."
	 *
	 * This could be snoring, restless shuffle, or any other interpretation.
	 */
	//public List<Pair<LocalTime, Duration>> noises;

	/**
	 * The user-reported description of this session, which would not fit
	 * into data buckets above.
	 */
	public String otherDescription;

	/**
	 * Sleep coefficient for overview module
	 */
	private double sleepCoefficient;

	//
	// GENERATED METHODS FOLLOW:
	//


	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public long getUntilDeepSleep() {
		return untilDeepSleep;
	}

	public void setUntilDeepSleep(long untilDeepSleep) {
		this.untilDeepSleep = untilDeepSleep;
	}

	public int getDreamType() {
		return dreamType;
	}

	public void setDreamType(int dreamType) {
		this.dreamType = dreamType;
	}

	public String getDreamDiary() {
		return dreamDiary;
	}

	public void setDreamDiary(String dreamDiary) {
		this.dreamDiary = dreamDiary;
	}

	public int getDiscomfortType() {
		return discomfortType;
	}

	public void setDiscomfortType(int discomfortType) {
		this.discomfortType = discomfortType;
	}

	public double getSleepCoefficient() { return this.sleepCoefficient; }

	public void setSleepCoefficient(double sleepCoefficient) {
		this.sleepCoefficient = sleepCoefficient;
	}
	/*
	public List<Pair<LocalTime, Duration>> getNoises() {
		return noises;
	}

	public void setNoises(List<Pair<LocalTime, Duration>> noises) {
		this.noises = noises;
	}
	//*/

	public String getOtherDescription() {
		return otherDescription;
	}

	public void setOtherDescription(String otherDescription) {
		this.otherDescription = otherDescription;
	}
}
