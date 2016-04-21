package com.sciencesquad.health.run;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by brandonschabell on 4/18/16.
 */
public class CompletedRunModel extends RealmObject{
    @Required
    private Date date;
    private double calories;
    private double distance;
    //TODO: GoogleMap object

    //GETTERS AND SETTERS
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    //TODO: GoogleMap Getter

    //TODO: GoogleMap Setter

}
