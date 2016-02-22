package com.sciencesquad.health.steps;

import com.sciencesquad.health.data.RealmDataContext;
import com.sciencesquad.health.data.RealmEmptyEvent;
import com.sciencesquad.health.data.RealmUpdateEvent;
import com.sciencesquad.health.events.BaseApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.annotations.RealmModule;

/**
 * Created by colin on 2/22/16.
 */

/**
 * Realm module for the Nutrition module. (RealmNutritionModule)
 * This will be the first example of Realm Integration.
 */
@RealmModule(classes = {StepsModel.class})
public class RealmStepsModule extends RealmDataContext {
    private static final String TAG = RealmStepsModule.class.getSimpleName();

    private RealmConfiguration configStepsRealm;
    private Realm realm;
    private RealmList<StepsModel> stepsModels;
    private RealmQuery<StepsModel> querySteps;

    /**
     * The Base application will supply the context.
     * Which is nice in setting up any context needed by this module
     * :) :)
     */
    public RealmStepsModule() {
        setRealmName("steps.realm");
        setRealmContext(BaseApplication.application().getApplicationContext());
        init();
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
    public void init() {
        configStepsRealm = new RealmConfiguration.Builder(getRealmContext())
                .name(getRealmName())
                .setModules(this)
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(configStepsRealm);
        stepsModels = new RealmList<>();
    }

    /**
     * This will update the realm asynchronously from the UI thread
     * and will update the model given that something has changed.
     * This will generate an event to all subscribers on the Event Bus.
     *
     * This can later be modified to write to a list of Realm Objects
     * which could be useful for a history.
     */
    @Override
    public void update() {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(stepsModels);
        realm.commitTransaction();

        BaseApplication.application().eventBus().publish(RealmUpdateEvent.from(this).key(returnRealmKey()).create());
    }

    /**
     *
     * This will set up a Realm Query object
     * based off of the Realm Object associated with the module
     *
     * From there, one can do SQL-esq queries which returns a
     * RealmList<RealmObjectClass> results,
     * which is pertinent to that query.
     */
    @Override
    public void query() {
        querySteps = realm.where(StepsModel.class);
    }

    /**
     *
     * This will clear all the relevant models from the realm.
     * This will generate an event to all subscribers on the Event Bus.
     * Use this with caution.
     */
    @Override
    public void clearRealm() {
        realm.beginTransaction();
        realm.clear(StepsModel.class);
        realm.commitTransaction();

        BaseApplication.application().eventBus().publish(RealmEmptyEvent.from(this).realmName(getRealmName()).create());
    }

    /**
     * This function should be called everytime the module is done being used.
     * Because closing files is the right thing to do preserve data.
     */
    @Override
    public void closeRealm() {
        realm.close();
    }

    /**
     * This will take a model that is stored in the realm
     * via a query then update the key to it.
     * This will generate an event to all subscribers on the Event Bus.
     * This can be also used to update other certain values one at a time.
     */
    @Override
    public void updateRealmModel(int index, int newKey) {
        realm.beginTransaction();
        StepsModel updateModel = querySteps.findAll().get(index);
        updateModel.setStepCount(newKey);
        realm.commitTransaction();
        BaseApplication.application().eventBus().publish(RealmUpdateEvent.from(this).key(returnRealmKey()).create());
    }

    /**
     * Returns a string representation of the primary key
     * stored in a particular realm.
     */
    @Override
    public String returnRealmKey() {
        return "stepCount";
    }

    /**
     * Returns a list of Models stored in a realm.
     */
    @Override
    public RealmList<StepsModel> getRealmList() {
        return stepsModels;
    }

    /**
     * Returns the most recent query format that was created query().
     */
    @Override
    public RealmQuery<StepsModel> getQuerySteps() {
        return querySteps;
    }

}
