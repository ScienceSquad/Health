package com.sciencesquad.health.health.database.events;

import com.sciencesquad.health.events.Event;

/**
 * Created by danielmiller on 2/19/16.
 *
 * Event for clearing a realm.
 * This means the database has been wiped.
 *
 */
public interface RealmEmpty extends Event {
    String RealmName();
}
