package com.sciencesquad.health.sleep;

import android.util.Pair;
import org.immutables.value.Value;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.util.List;

/**
 * The model that records the user's sleep through a period of time,
 * along with data, as a session. This data is in accordance with the
 * Pittsburgh Sleep Quality Index (PSQI) and can be used for such
 * calculation or pattern learning.
 */
@Value.Immutable
public interface SleepDataModel {

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
	LocalDate date();

	/**
	 * When the sleep interval began.
	 */
	LocalTime start();

	/**
	 * When the sleep interval ended.
	 */
	LocalTime end();

	/**
	 * The time taken between beginning the session and actually entering
	 * deep sleep (usually marked as REM sleep).
	 */
	Duration untilDeepSleep();

	/**
	 * The user-reported type of dream that occurred during this session.
	 * (For either sleep or a short nap.)
	 */
	DreamType dreamType();

	/**
	 * The user-reported dream diary for this session.
	 * This should only be for the user to review; not for number crunching.
	 */
	String dreamDiary();

	/**
	 * The user-reported type of discomfort (or comfort) that occurred
	 * during this session.
	 *
	 * If the user reports something like "do not remember," use
	 * ComfortType.UNKNOWN. If they report "comfortable" or "nothing
	 * in particular," use ComfortType.NONE.
	 */
	DiscomfortType discomfortType();

	/**
	 * A list of all (time, duration) tuples that correspond to points
	 * during this session of sleep, where a noise above the ambient threshold
	 * was detected and confirmed by the user as a "sleep noise."
	 *
	 * This could be snoring, restless shuffle, or any other interpretation.
	 */
	List<Pair<LocalTime, Duration>> noises();

	/**
	 * The user-reported description of this session, which would not fit
	 * into data buckets above.
	 */
	String otherDescription();
}
