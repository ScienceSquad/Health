package com.sciencesquad.health.run;

import io.realm.RealmObject;

/**
 * Created by brandonschabell on 4/18/16.
 */
public class CreatedRouteModel extends RealmObject{
    private double distance;
    //TODO: GoogleMap object

    //GETTERS AND SETTERS

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    //TODO: GoogleMap Getter

    //TODO: GoogleMap Setter
}
