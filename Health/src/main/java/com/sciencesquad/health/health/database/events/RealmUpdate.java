package com.sciencesquad.health.health.database.events;

import com.sciencesquad.health.events.Event;

import org.immutables.value.Value;

/**
 * Created by danielmiller on 2/19/16.
 *
 * Event for updating a Realm in any abstract way.
 */
@Event.EventType @Value.Immutable
public interface RealmUpdate extends Event {
    String key();
}
