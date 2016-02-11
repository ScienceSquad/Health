package com.sciencesquad.health.health.Nutrition;


import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
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

    private RealmConfiguration configNutritionRealm;
    private Realm realm;

    public RealmNutritionModule(){
        setRealmName("nutrition.realm");
    }

    @Override
    public void init(Context context){
        RealmNutritionModule nutritionModule = new RealmNutritionModule();
        configNutritionRealm = new RealmConfiguration.Builder(context)
                .name(getRealmName())
                .setModules(nutritionModule)
                .build();

        realm = Realm.getInstance(configNutritionRealm);
    }

    @Override
    public void update(){

    }

    @Override
    public void query(){

    }

}
