package com.sciencesquad.health.data;

import android.content.Context;
import com.sciencesquad.health.events.Event;

import io.realm.RealmList;
import io.realm.RealmQuery;
import org.immutables.value.Value;

/**
 * This is an abstract database context specifically for a Realm database.
 * Everything a Realm Database must do goes here.
 *
 * Things must be implemented.
 * - Everything from DataContext
 * - Clearing a realm.
 * - Updating a single Realm model.
 * - Closing a realm.
 * - Returning a String representation of the Primary key in a Realm.
 */
public abstract class RealmDataContext implements DataContext {
    private static final String TAG = RealmDataContext.class.getSimpleName();

    /**
     * Event for clearing a realm.
     * This means the database has been wiped.
     */
    @Value.Immutable @Event.EventType
    public interface RealmEmpty extends Event {
        String realmName();
    }

    /**
     * Event for updating a Realm in any abstract way.
     */
    @Event.EventType @Value.Immutable
    public interface RealmUpdate extends Event {
        String key();
    }

    private String realmName;
    private Context realmContext;

    public abstract void clearRealm();
    public abstract void closeRealm();
    public abstract String returnRealmKey();
    public abstract RealmList getRealmList();
    public abstract RealmQuery getQueryNotation();

	//
    // GETTERS AND SETTERS
	//

    public void setRealmName(String filename){
        this.realmName = filename;
    }

    public String getRealmName() {
		return this.realmName;
	}

    public void setRealmDataContext(Context context) {
        this.realmContext = context;
    }

}
