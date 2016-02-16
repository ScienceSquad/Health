package com.sciencesquad.health.health.Nutrition;


import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.annotations.RealmModule;
import com.sciencesquad.health.health.database.*;

import java.util.List;

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

        // set up initial model.
        realm.beginTransaction();
        nutritionModels.add(new RealmNutritionModel());
        realm.commitTransaction();
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
        realm.executeTransaction(new Realm.Transaction() {
            // because Lambda's are too hard for me apparently.
            @Override
            public void execute(Realm realm1) {
                // copy or update the object.
                realm1.copyToRealmOrUpdate(nutritionModels);
            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                // publish write was successful.
            }

            @Override
            public void onError(Exception e) {
                // realm transaction is automatically cancelled.
                // publish write failed and other clean up duties.
            }
        });
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


    public RealmList<RealmNutritionModel> getNutritionModelList() {
        return nutritionModels;
    }
    public RealmQuery<RealmNutritionModel> getQueryNutrition(){
        return queryNutrition;
    }
}
