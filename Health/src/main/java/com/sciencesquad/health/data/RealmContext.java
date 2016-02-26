package com.sciencesquad.health.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sciencesquad.health.events.BaseApplication;
import io.realm.*;
import java8.util.function.Consumer;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * The RealmContext.
 */
public final class RealmContext<M extends RealmObject> implements DataContext<M> {
    private static final String TAG = RealmContext.class.getSimpleName();

	/**
	 * List of Failure strings for Data Failure.
	 */
	public static class Failures{
		public static final String COULD_NOT_INIT_REALM = "COULD_NOT_INIT_REALM";
		public static final String COULD_NOT_ADD_SINGLE_OBJECT = "COULD_NOT_ADD_SINGLE_OBJECT";
		public static final String COULD_NOT_ADD_COLLECTION_OBJECT = "COULD_NOT_ADD_COLLECTION_OBJECT";
		public static final String COULD_NOT_REMOVE_SINGLE_OBJECT = "COULD_NOT_REMOVE_SINGLE_OBJECT ";
		public static final String COULD_NOT_REMOVE_COLLECTION_OBJECT = "COULD_NOT_REMOVE_COLLECTION_OBJECT";
		public static final String OBJECT_DOES_NOT_EXIST = "OBJECT_DOES_NOT_EXIST";
		public static final String OBJECT_COLLECTION_DOES_NOT_EXIST = "OBJECT_COLLECTION_DOES_NOT_EXIST";
		public static final String COULD_NOT_PRODUCE_HASH_CODE = "COULD_NOT_PRODUCE_HASH_CODE";
		public static final String COULD_NOT_CLEAR_REALM = "COULD_NOT_CLEAR_REALM";
		public static final String COULD_NOT_UPDATE_REALM_AT_INDEX = "COULD_NOT_UPDATE_REALM_AT_INDEX";
		public static final String COULD_NOT_COMPARE_OBJECT = "COULD_NOT_COMPARE_OBJECT";
		public static final String COULD_NOT_PRODUCE_ITERATOR = "COULD_NOT_PRODUCE_ITERATOR";
		public static final String COULD_NOT_RETAIN_OBJECTS = "COULD_NOT_RETAIN_OBJECTS";
		public static final String COULD_NOT_PRODUCE_QUERY = "COULD_NOT_PRODUCE_QUERY";
		public static final String OBJECT_CANNOT_CHECK_ISEMPTY = "OBJECT_CANNOT_CHECK_ISEMPTY";
		public static final String OBJECT_HAS_NO_SIZE = "OBJECT_HAS_NO_SIZE";
		public static final String COULD_NOT_PRODUCE_ARRAY = "COULD_NOT_PRODUCE_ARRAY";
	}

	private Realm realm;
	private Class<M> realmClass;

	// Shorthand for what we need -- RealmResults for everything.
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
		try {
			Log.d(TAG, "Initing realm for this model: " + realmClass.getSimpleName());
			RealmConfiguration config = new RealmConfiguration.Builder(context)
					.name(identifier)
					.deleteRealmIfMigrationNeeded() // DEBUG ONLY
					.build();
			this.realm = Realm.getInstance(config);
			this.realmClass = realmClass;
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.COULD_NOT_INIT_REALM).create());
		}
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
		try {
			realm.beginTransaction();
			this.realm.copyToRealm(object);
			realm.commitTransaction();
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.COULD_NOT_ADD_SINGLE_OBJECT).create());
			return false;
		}
		return true;
    }

	/**
	 * @see Collection
	 */
	@Override
	public boolean addAll(Collection<? extends M> collection) {
		try {
			realm.beginTransaction();
			this.realm.copyToRealm(collection);
			realm.commitTransaction();
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.COULD_NOT_ADD_COLLECTION_OBJECT).create());
			return false;
		}
		return true;
	}

	/**
	 * @see Collection
	 */
	@Override
	public boolean contains(Object object) {
		try {
			return items().contains(object);
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.OBJECT_DOES_NOT_EXIST).create());
			return false;
		}
	}

	/**
	 * @see Collection
	 */
	@Override
	public boolean containsAll(@NonNull Collection<?> collection) {
		try {
			return items().containsAll(collection);
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.OBJECT_COLLECTION_DOES_NOT_EXIST).create());
			return false;
		}
	}

	/**
	 * @see Collection
	 */
	@Override
	public boolean equals(Object object) {
		try {
			return items().equals(object);
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.COULD_NOT_COMPARE_OBJECT).create());
			return false;
		}
	}

	/**
	 * @see Collection
	 */
	@Override
	public int hashCode() {
		try {
			return items().hashCode();
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.COULD_NOT_PRODUCE_HASH_CODE).create());
			return -1;
		}
	}

	/**
	 * @see Collection
	 */
	@Override
	public boolean isEmpty() {
		try {
			return items().isEmpty();
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.OBJECT_CANNOT_CHECK_ISEMPTY).create());
			return false;
		}
	}

	/**
	 * @see Collection
	 */
	@Override @NonNull
	public Iterator<M> iterator() {
		try {
			return items().iterator();
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.COULD_NOT_PRODUCE_ITERATOR).create());
			return Collections.emptyIterator();
		}

	}

	/**
	 * @see Collection
	 */
	// Note: Realm doesn't do remove() well.
	@Override
	public boolean remove(Object object) {
		try {
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
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.COULD_NOT_REMOVE_SINGLE_OBJECT).create());
			return false;
		}
	}

	/**
	 * @see Collection
	 */
	// Note: Realm doesn't do remove() well.
	@Override
	public boolean removeAll(@NonNull Collection<?> collection) {
		try {
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
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.COULD_NOT_REMOVE_COLLECTION_OBJECT).create());
			return false;
		}
	}

	/**
	 * @see Collection
	 */
	// Note: Realm doesn't do remove() well.
	@Override
	public boolean retainAll(@NonNull Collection<?> collection) {
		try {
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
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.COULD_NOT_RETAIN_OBJECTS).create());
			return false;
		}
	}

	/**
	 * @see Collection
	 */
	@Override
	public int size() {
		try {
			return items().size();
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.OBJECT_HAS_NO_SIZE).create());
			return -1;
		}
	}

	/**
	 * @see Collection
	 */
	public void clear() {
		try {
			realm.beginTransaction();
			realm.clear(this.realmClass);
			realm.commitTransaction();
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.COULD_NOT_CLEAR_REALM).create());
		}
	}

	/**
	 * @see Collection
	 */
	@Override @NonNull
	public Object[] toArray() {
		try {
			return items().toArray();
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.COULD_NOT_PRODUCE_ARRAY).create());
			return (new Object[1]);
		}
	}

	/**
	 * @see Collection
	 */
	// Note: Dunno how to handle this type of failure.
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
	@Nullable
    public RealmQuery<M> query() {
		try {
			return realm.where(this.realmClass);
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.COULD_NOT_PRODUCE_QUERY).create());
			return null;
		}
    }


    /**
     * This will take a model that is stored in the realm
     * via a query then update the key to it.
     * This will generate an event to all subscribers on the Event Bus.
     * This can be also used to update other certain values one at a time.
     */
    public void updateRealmModel(int index, Consumer<M> handler) {
		try {
			realm.beginTransaction();
			handler.accept(items().get(index));
			realm.commitTransaction();
		} catch (Exception e) {
			BaseApplication.application().eventBus().publish(DataFailureEvent.from(this).operation(Failures.COULD_NOT_UPDATE_REALM_AT_INDEX).create());
		}
    }
}
