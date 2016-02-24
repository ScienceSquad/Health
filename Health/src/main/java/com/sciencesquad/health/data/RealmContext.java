package com.sciencesquad.health.data;

import android.content.Context;
import com.sciencesquad.health.events.BaseApplication;
import com.sciencesquad.health.events.Event;
import com.sciencesquad.health.nutrition.NutritionModel;
import io.realm.*;
import java8.util.function.Consumer;
import org.immutables.value.Value;

import java.util.List;

/**
 * Realm module for the Nutrition module.
 * This will be the first example of Realm Integration.
 */
public final class RealmContext<M extends RealmObject> implements DataContext {
    private static final String TAG = RealmContext.class.getSimpleName();

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

	private Realm realm;
	private String realmName;

	private Class<M> clazz;
    private RealmList<M> listOfModels;
    private RealmQuery<M> queryNotation;

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
    public void init(Context context, Class clazz, String identifier) {
		RealmConfiguration config = new RealmConfiguration.Builder(context)
				.name(identifier)
				.deleteRealmIfMigrationNeeded() // DEBUG ONLY
				.build();

        this.realmName = identifier;
		this.clazz = clazz;
        realm = Realm.getInstance(config);
        listOfModels = new RealmList<>();
    }

    /**
     * This will update the realm and will update the model given that something has changed.
     * This will generate an event to all subscribers on the Event Bus.
     *
     * This can later be modified to write to a list of Realm Objects
     * which could be useful for a history.
     */
    @Override
    public void update() {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(listOfModels);
        realm.commitTransaction();

        BaseApplication.application().eventBus().publish(RealmUpdateEvent.from(this).key(returnRealmKey()).create());
    }

    /**
     *
     * This will set up a Realm Query object
     * based off of the Realm Object associated with the module
     *
     * From there, one can do SQL-esque queries which returns a
     * RealmList<RealmObjectClass> results,
     * which is pertinent to that query.
     */
    @Override
	@SuppressWarnings("unchecked") // forgive me for these sins
    public void query() {
        queryNotation = realm.where(this.clazz);
    }

    /**
     *
     * This will clear all the relevant models from the realm.
     * This will generate an event to all subscribers on the Event Bus.
     * Use this with caution.
     */

    public void clearRealm() {
        realm.beginTransaction();
        realm.clear(NutritionModel.class);
        realm.commitTransaction();

        BaseApplication.application().eventBus().publish(RealmEmptyEvent.from(this).realmName(realmName).create());
    }

    /**
     * This function should be called every time the module is done being used.
     * Because closing files is the right thing to do preserve data.
     */
    public void closeRealm() {
        realm.close();
    }

    /**
     * This will take a model that is stored in the realm
     * via a query then update the key to it.
     * This will generate an event to all subscribers on the Event Bus.
     * This can be also used to update other certain values one at a time.
     */

    public void updateRealmModel(int index, Consumer<M> handler) {
        realm.beginTransaction();
		handler.accept(queryNotation.findAll().get(index));
        realm.commitTransaction();

        BaseApplication.application().eventBus().publish(RealmUpdateEvent.from(this).key(returnRealmKey()).create());
    }

    /**
     * Returns a string representation of the primary key
     * stored in a particular realm.
     */

    public String returnRealmKey() {
        if (realmName.equals("nutrition.realm"))
            return "calorieIntake";
        else if (realmName.equals("steps.realm"))
            return "stepCount";
        else
            return "";
    }

    /**
     * Returns a list of Models stored in a realm.
     */

    public List<M> getList() {
        return listOfModels;
    }

    /**
     * Returns the most recent query format that was created query().
     */

    public RealmQuery<M> getQueryNotation() {
        return queryNotation;
    }
}
