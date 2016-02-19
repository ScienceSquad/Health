package com.sciencesquad.health.health.Nutrition;


import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.annotations.RealmModule;
import com.sciencesquad.health.health.database.*;


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

    public RealmNutritionModule(Context context){
        setRealmName("nutrition.realm");
        setRealmContext(context);
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
         *
         * This can later be modified to write to a list of Realm Objects
         * which could be useful for a history.
         */
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(nutritionModels);
        realm.commitTransaction();

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
        realm.beginTransaction();
        realm.clear(RealmNutritionModel.class);
        realm.commitTransaction();
    }

    @Override
    public void closeRealm() {
        realm.close();
    }

    public RealmList<RealmNutritionModel> getNutritionModelList() {
        return nutritionModels;
    }
    public RealmQuery<RealmNutritionModel> getQueryNutrition(){
        return queryNutrition;
    }

    @Override
    public void updateRealmModel(int index, int newKey){
        realm.beginTransaction();
        RealmNutritionModel updateModel = queryNutrition.findAll().get(index);
        updateModel.setCalorieIntake(newKey);
        realm.commitTransaction();
    }

}
