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
    private double magnesium;
    private double folicAcid;
    private double chromium;
    private double selenium;

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

    public double getSelenium() {
        return selenium;
    }

    public void setSelenium(double selenium) {
        this.selenium = selenium;
    }

    public double getChromium() {
        return chromium;
    }

    public void setChromium(double chromium) {
        this.chromium = chromium;
    }

    public double getFolicAcid() {
        return folicAcid;
    }

    public void setFolicAcid(double folicAcid) {
        this.folicAcid = folicAcid;
    }

    public double getMagnesium() {
        return magnesium;
    }

    public void setMagnesium(double magnesium) {
        this.magnesium = magnesium;
    }
}
