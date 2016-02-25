package com.sciencesquad.health.data;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sciencesquad.health.events.BaseApplication;
import io.realm.*;
import java8.util.function.Consumer;

import java.util.Iterator;

/**
 * The RealmContext.
 */
public final class RealmContext<M extends RealmObject> extends DataContext<M> {
    private static final String TAG = RealmContext.class.getSimpleName();

	private Realm realm;
	private String realmName;
	private Class<M> realmClass;

    /**
     * This sets up the Realm for the module.
     *
     * Notes:
     *      - sets up a RealmConfiguration to establish the realm.
     *      - uses RealmConfiguration to build the Realm for this module.
     *      - Sets up a RealmList to hold all the RealmModels in a list for history purposes.
     *      - configNutritionRealm is set to delete the Realm if there are changes in the model,
     *      which in the early stages of development is okay.
     *      HOWEVER, once this is released, we need to make a separate Migration, which is will
     *      support multiple versions of the application.
     */
    @Override
	@SuppressWarnings("unchecked")
    public void init(Context context, Class realmClass, String identifier) {
		RealmConfiguration config = new RealmConfiguration.Builder(context)
				.name(identifier)
				.deleteRealmIfMigrationNeeded() // DEBUG ONLY
				.build();

        this.realm = Realm.getInstance(config);
		this.realmName = identifier;
		this.realmClass = realmClass;
    }

    /**
     * This will update the realm and will update the model given that something has changed.
     * This will generate an event to all subscribers on the Event Bus.
     *
     * This can later be modified to write to a list of Realm Objects
     * which could be useful for a history.
     */
    @Override
    public boolean add(M object) {
        realm.beginTransaction();
		this.realm.copyToRealm(object);
        realm.commitTransaction();

        //BaseApplication.application().eventBus().publish(RealmUpdateEvent.from(this).key("FIXME").create());
		return true;
    }

	@Override
	public boolean contains(Object object) {
		realm.beginTransaction();
		boolean result = false;
		RealmResults<M> results = realm.where(this.realmClass).findAll();
		for (M m : results) {
			if (m.equals(object)) {
				result = true;
				break;
			}
		}
		realm.commitTransaction();
		return result;
	}

	@Override @NonNull
	public Iterator<M> iterator() {
		realm.beginTransaction();
		Iterator<M> it = this.realm.where(this.realmClass).findAll().iterator();
		realm.commitTransaction();
		return it;
	}

	@Override
	public boolean remove(Object object) {
		realm.beginTransaction();
		RealmResults<M> results = realm.where(this.realmClass).findAll();
		for (M m : results) {
			if (m.equals(object)) {
				results.remove(m);
				break;
			}
		}
		realm.commitTransaction();
		return true;
	}

	@Override
	public int size() {
		realm.beginTransaction();
		long result = this.realm.where(this.realmClass).count();
		realm.commitTransaction();
		return (int)result;
	}

	/**
     * This will set up a Realm Query object
     * based off of the Realm Object associated with the module
     *
     * From there, one can do SQL-esque queries which returns a
     * RealmList<RealmObjectClass> results,
     * which is pertinent to that query.
     */

	@SuppressWarnings("unchecked")
    public RealmQuery<M> query() {
        return realm.where(this.realmClass);
    }

    /**
     * This will clear all the relevant models from the realm.
     * This will generate an event to all subscribers on the Event Bus.
     * Use this with caution.
     */
    public void clear() {
        realm.beginTransaction();
        realm.clear(this.realmClass);
        realm.commitTransaction();

        BaseApplication.application().eventBus().publish(DataEmptyEvent.from(this).realmName(realmName).create());
    }

    /**
     * This function should be called every time the module is done being used.
     * Because closing files is the right thing to do preserve data.
     */
    public void close() throws Exception {
        realm.close();
    }

	@Override
	public void update() {

	}


    /**
     * This will take a model that is stored in the realm
     * via a query then update the key to it.
     * This will generate an event to all subscribers on the Event Bus.
     * This can be also used to update other certain values one at a time.
     */
    public void updateRealmModel(int index, Consumer<M> handler) {
        realm.beginTransaction();
		handler.accept(this.query().findAll().get(index));
        realm.commitTransaction();

        BaseApplication.application().eventBus().publish(DataUpdateEvent.from(this).key("FIXME").create());
    }
}
