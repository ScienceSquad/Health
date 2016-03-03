package com.sciencesquad.health.nutrition;

import io.realm.RealmObject;

/**
 * Supplement to the NutritionModel
 * Stores all the relevant minerals the user may need.
 * Stored as percentages based on a 2,000 calorie diet.
 */
public class MineralModel extends RealmObject {

    private double calcium;
    private double iron;
    private double potassium;
    private double zinc;

    public double getCalcium() {
        return calcium;
    }

    public void setCalcium(double calcium) {
        this.calcium = calcium;
    }

    public double getIron() {
        return iron;
    }

    public void setIron(double iron) {
        this.iron = iron;
    }

    public double getPotassium() {
        return potassium;
    }

    public void setPotassium(double potassium) {
        this.potassium = potassium;
    }

    public double getZinc() {
        return zinc;
    }

    public void setZinc(double zinc) {
        this.zinc = zinc;
    }
}
