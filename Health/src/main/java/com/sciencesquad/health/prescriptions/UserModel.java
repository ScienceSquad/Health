package com.sciencesquad.health.prescriptions;

import android.support.annotation.NonNull;

import com.sciencesquad.health.workout.RealmString;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by mrjohnson on 4/8/16.
 */
public class UserModel extends RealmObject {
    @PrimaryKey
    private String name;
    private float weight;
    /**
    * Calendar date where this model was created.
    */
    @Required
    private Date date;
    //private Date birthDate;
    private int age;

    public String getName(){
            return name;
        }
    public void setName(String name) {
            this.name = name;
        }

    public float getWeight(){ return this.weight; }
    public void setWeight(float weight){ this.weight = weight; }

    //public Date getbirthDate(){ return this.birthDate; }
    //public void setBirthDate(Date birthDate){ this.birthDate = birthDate; }

    public int getAge(){ return this.age; }
    public void setAge(int age){ this.age = age; }



    @NonNull
    public Date getDate() {
            return date;
        }

    public void setDate(@NonNull Date date) {
            this.date = date;
        }

    @Override
    public String toString() {
        return getName() + "," + getAge() + "," + getWeight();
    }

    public UserModel fromString(String stringed){
        UserModel user = new UserModel();
        String[] array = stringed.split(",");
        user.setName(array[0]);
        user.setAge(Integer.parseInt(array[1]));
        user.setWeight(Float.parseFloat(array[2]));
        return  user;
    }
}
