package com.sciencesquad.health.data;

import android.content.Context;
import android.support.annotation.NonNull;
import io.realm.*;
import java8.util.function.Consumer;

import java.util.Collection;
import java.util.Iterator;

/**
 * The RealmContext.
 */
public final class RealmContext<M extends RealmObject> implements DataContext<M> {
    private static final String TAG = RealmContext.class.getSimpleName();

	private Realm realm;
	private Class<M> realmClass;

	private RealmResults<M> items() {
		return this.realm.where(this.realmClass).findAll();
	}

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
		this.realmClass = realmClass;
    }

	/**
	 * Return the Realm for this RealmContext.
	 * @return the Realm underlying this RealmContext
	 */
	@NonNull
	public Realm getRealm() {
		return this.realm;
	}

	/**
	 * @see Collection
	 */
    @Override
    public boolean add(M object) {
        realm.beginTransaction();
		this.realm.copyToRealm(object);
        realm.commitTransaction();

		return true;
    }

	/**
	 * @see Collection
	 */
	@Override
	public boolean addAll(Collection<? extends M> collection) {
		realm.beginTransaction();
		this.realm.copyToRealm(collection);
		realm.commitTransaction();
		return true;
	}

	/**
	 * @see Collection
	 */
	@Override
	public boolean contains(Object object) {
		return items().contains(object);
	}

	/**
	 * @see Collection
	 */
	@Override
	public boolean containsAll(@NonNull Collection<?> collection) {
		return items().containsAll(collection);
	}

	/**
	 * @see Collection
	 */
	@Override
	public boolean equals(Object object) {
		return items().equals(object);
	}

	/**
	 * @see Collection
	 */
	@Override
	public int hashCode() {
		return items().hashCode();
	}

	/**
	 * @see Collection
	 */
	@Override
	public boolean isEmpty() {
		return items().isEmpty();
	}

	/**
	 * @see Collection
	 */
	@Override @NonNull
	public Iterator<M> iterator() {
		return items().iterator();
	}

	/**
	 * @see Collection
	 */
	// Note: Realm doesn't do remove() well.
	@Override
	public boolean remove(Object object) {
		realm.beginTransaction();
		RealmResults<M> results = items();
		for (M m : results) {
			if (m.equals(object)) {
				results.remove(m);
				break;
			}
		}
		realm.commitTransaction();
		return true;
	}

	/**
	 * @see Collection
	 */
	// Note: Realm doesn't do remove() well.
	@Override
	public boolean removeAll(@NonNull Collection<?> collection) {
		realm.beginTransaction();
		RealmResults<M> results = items();
		for (Object object : collection) {
			for (M m : results) {
				if (m.equals(object)) {
					results.remove(m);
					break;
				}
			}
		}
		realm.commitTransaction();
		return true;
	}

	/**
	 * @see Collection
	 */
	// Note: Realm doesn't do remove() well.
	@Override
	public boolean retainAll(@NonNull Collection<?> collection) {
		realm.beginTransaction();
		RealmResults<M> results = items();
		for (M m : results) {
			if (!collection.contains(m)) {
				results.remove(m);
				break;
			}
		}
		realm.commitTransaction();
		return true;
	}

	/**
	 * @see Collection
	 */
	@Override
	public int size() {
		return items().size();
	}

	/**
	 * @see Collection
	 */
	public void clear() {
		realm.beginTransaction();
		realm.clear(this.realmClass);
		realm.commitTransaction();
	}

	/**
	 * @see Collection
	 */
	@Override @NonNull
	public Object[] toArray() {
		return items().toArray();
	}

	/**
	 * @see Collection
	 */
	@Override @NonNull
	public <T> T[] toArray(@NonNull T[] array) {
		return items().toArray(array);
	}

	/**
	 * @see AutoCloseable
	 */
	public void close() throws Exception {
		realm.close();
	}

	/**
     * This will set up a Realm Query object
     * based off of the Realm Object associated with the module
     *
     * From there, one can do SQL-esque queries which returns a
     * RealmList<RealmObjectClass> results,
     * which is pertinent to that query.
     */
    public RealmQuery<M> query() {
        return realm.where(this.realmClass);
    }


    /**
     * This will take a model that is stored in the realm
     * via a query then update the key to it.
     * This will generate an event to all subscribers on the Event Bus.
     * This can be also used to update other certain values one at a time.
     */
    public void updateRealmModel(int index, Consumer<M> handler) {
        realm.beginTransaction();
		handler.accept(items().get(index));
        realm.commitTransaction();
    }
}
