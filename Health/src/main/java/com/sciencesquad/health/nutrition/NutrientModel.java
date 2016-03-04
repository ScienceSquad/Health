package com.sciencesquad.health.nutrition;

import io.realm.RealmObject;

/**
 * Supplement to the Nutrition model.
 * Holds all relevant nutrients for the user.
 * Will be organized by measurement (grams vs. milligrams)
 */
public class NutrientModel extends RealmObject {

    // in grams
    private int totalFat;
    private int saturatedFats;
    private int transFats;
    private int totalCarbs;
    private int dietaryFiber;
    private int sugars;
    private int protein;

    // in milligrams
    private int cholesterol;
    private int sodium;

    public int getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(int totalFat) {
        this.totalFat = totalFat;
    }

    public int getSaturatedFats() {
        return saturatedFats;
    }

    public void setSaturatedFats(int saturatedFats) {
        this.saturatedFats = saturatedFats;
    }

    public int getTransFats() {
        return transFats;
    }

    public void setTransFats(int transFats) {
        this.transFats = transFats;
    }

    public int getTotalCarbs() {
        return totalCarbs;
    }

    public void setTotalCarbs(int totalCarbs) {
        this.totalCarbs = totalCarbs;
    }

    public int getDietaryFiber() {
        return dietaryFiber;
    }

    public void setDietaryFiber(int dietaryFiber) {
        this.dietaryFiber = dietaryFiber;
    }

    public int getSugars() {
        return sugars;
    }

    public void setSugars(int sugars) {
        this.sugars = sugars;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public int getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(int cholesterol) {
        this.cholesterol = cholesterol;
    }

    public int getSodium() {
        return sodium;
    }

    public void setSodium(int sodium) {
        this.sodium = sodium;
    }
}
