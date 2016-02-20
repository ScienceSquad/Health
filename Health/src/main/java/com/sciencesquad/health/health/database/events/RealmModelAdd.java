package com.sciencesquad.health.health.database.events;

import com.sciencesquad.health.events.*;

import org.immutables.value.Value;

/**
 * Created by danielmiller on 2/19/16.
 *
 * Event for adding a RealmModel to some realm.
 */
@Event.EventType @Value.Immutable
public interface RealmModelAdd extends Event {
    String key();
}
