package com.sciencesquad.health.health.Nutrition;


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

    @Override
    public void init(){

    }
    @Override
    public void update(){

    }
    @Override
    public void query(){

    }

}
