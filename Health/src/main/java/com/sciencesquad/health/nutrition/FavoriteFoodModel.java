package com.sciencesquad.health.nutrition;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by danielmiller on 4/16/16.
 */
public class FavoriteFoodModel extends RealmObject {
    @PrimaryKey
    private String name;

    public FavoriteFoodModel(){
        this.name = "";
    }

    public FavoriteFoodModel(String name){
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
