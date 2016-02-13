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

    private static final String TAG = "Realm Nutrition Module";

    private RealmConfiguration configNutritionRealm;
    private Realm realm;

    public RealmNutritionModule(Context context){
        setRealmName("nutrition.realm");
        setRealmContext(context);
        init();
    }

    @Override
    public void init(){
        configNutritionRealm = new RealmConfiguration.Builder(getRealmContext())
                .name(getRealmName())
                .setModules(this)
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
