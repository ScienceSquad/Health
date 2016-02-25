package com.sciencesquad.health.steps;

import android.util.Pair;

import org.immutables.value.Value;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.util.List;

/**
 * The model that records the user's steps over a period of time,
 * along with data, as a session.
 */
@Value.Immutable
public interface StepsDataModel {

    /**
     * The date this session occurred.
     */
    LocalDate date();

    /**
     * When the step interval began.
     */
    LocalTime start();

    /**
     * When the step interval ended.
     */
    LocalTime end();

    /**
     * The user-reported description of this session, which would not fit
     * into data buckets above.
     */
    String otherDescription();
}

