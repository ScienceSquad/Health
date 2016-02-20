package com.sciencesquad.health.health.Nutrition;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.annotations.RealmModule;

import com.sciencesquad.health.BaseApplication;
import com.sciencesquad.health.health.database.*;
import com.sciencesquad.health.health.database.events.*;


/**
 * Created by danielmiller on 2/11/16.
 *
 * Realm module for the Nutrition module.
 * This will be the first example of Realm Integration.
 */

@RealmModule(classes = {RealmNutritionModel.class})
public class RealmNutritionModule extends RealmDataContext{
    private static final String TAG = "Realm Nutrition Module";

    private RealmConfiguration configNutritionRealm;
    private Realm realm;
    private RealmList<RealmNutritionModel> nutritionModels;
    private RealmQuery<RealmNutritionModel> queryNutrition;

    public RealmNutritionModule(){
        /**
         * The Base application will supply the context.
         * Which is nice in setting up any context needed by this module
         * :)
         */

        setRealmName("nutrition.realm");
        setRealmContext(BaseApplication.application().getApplicationContext());
        init();
    }

    @Override
    public void init(){

        /**
         * This sets up the Realm for the module.
         *
         * For now, this will only create one Realm Object
         * and write it to the Module's realm.
         * Making it so that it can create and write multiple Realm Objects
         * could be useful when doing a look at the user's history.
         */

        configNutritionRealm = new RealmConfiguration.Builder(getRealmContext())
                .name(getRealmName())
                .setModules(this)
                .build();

        realm = Realm.getInstance(configNutritionRealm);
        nutritionModels = new RealmList<>();
    }

    @Override
    public void update(){

        /**
         * This will update the realm asynchronously from the UI thread
         * and will update the model given that something has changed.
         * This will generate an event to all subscribers on the Event Bus.
         *
         * This can later be modified to write to a list of Realm Objects
         * which could be useful for a history.
         */
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(nutritionModels);
        realm.commitTransaction();

        BaseApplication.application().eventBus().publish(RealmUpdateEvent.from(this).key(returnRealmKey()).create());
    }

    @Override
    public void query(){
        /**
         *
         * This will set up a Realm Query object
         * based off of the Realm Object associated with the module
         *
         * From there, one can do SQL-esq queries which returns a
         * RealmList<RealmObjectClass> results,
         * which is pertinent to that query.
         */

        queryNutrition = realm.where(RealmNutritionModel.class);

    }

    @Override
    public void clearRealm(){
        /**
         *
         * This will clear all the relevant models from the realm.
         * This will generate an event to all subscribers on the Event Bus.
         * Use this with caution.
         */

        realm.beginTransaction();
        realm.clear(RealmNutritionModel.class);
        realm.commitTransaction();

        BaseApplication.application().eventBus().publish(RealmEmptyEvent.from(this).RealmName(getRealmName()).create());
    }

    @Override
    public void closeRealm() {

        /**
         * This function should be called everytime the module is done being used.
         * Because closing files is the right thing to do preserve data.
         */

        realm.close();
    }

    @Override
    public void updateRealmModel(int index, int newKey){
        /**
         * This will take a model that is stored in the realm
         * via a query then update the key to it.
         * This will generate an event to all subscribers on the Event Bus.
         * This can be also used to update other certain values one at a time.
         */

        realm.beginTransaction();
        RealmNutritionModel updateModel = queryNutrition.findAll().get(index);
        updateModel.setCalorieIntake(newKey);
        realm.commitTransaction();
        BaseApplication.application().eventBus().publish(RealmUpdateEvent.from(this).key(returnRealmKey()).create());
    }

    @Override
    public String returnRealmKey(){
        /**
         * Returns a string representation of the primary key
         * stored in a particular realm.
         */

        return "calorieIntake";
    }

    public RealmList<RealmNutritionModel> getNutritionModelList() {
        /**
         * Returns a list of Models stored in a realm.
         */

        return nutritionModels;
    }
    public RealmQuery<RealmNutritionModel> getQueryNutrition(){
        /**
         * Returns the most recent query format that was created query().
         */

        return queryNutrition;
    }

}
