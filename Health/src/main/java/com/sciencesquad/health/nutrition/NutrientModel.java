package com.sciencesquad.health.nutrition;

import io.realm.RealmObject;

/**
 * Supplement to the Nutrition model.
 * Holds all relevant nutrients for the user.
 * Will be organized by measurement (grams vs. milligrams)
 */
public class NutrientModel extends RealmObject {

    // in grams
    private float totalFat;
    private float saturatedFats;
    private float transFats;
    private float totalCarbs;
    private float dietaryFiber;
    private float sugars;
    private float protein;

    // in milligrams
    private float cholesterol;
    private float sodium;

    public float getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(float totalFat) {
        this.totalFat = totalFat;
    }

    public float getSaturatedFats() {
        return saturatedFats;
    }

    public void setSaturatedFats(float saturatedFats) {
        this.saturatedFats = saturatedFats;
    }

    public float getTransFats() {
        return transFats;
    }

    public void setTransFats(float transFats) {
        this.transFats = transFats;
    }

    public float getTotalCarbs() {
        return totalCarbs;
    }

    public void setTotalCarbs(float totalCarbs) {
        this.totalCarbs = totalCarbs;
    }

    public float getDietaryFiber() {
        return dietaryFiber;
    }

    public void setDietaryFiber(float dietaryFiber) {
        this.dietaryFiber = dietaryFiber;
    }

    public float getSugars() {
        return sugars;
    }

    public void setSugars(float sugars) {
        this.sugars = sugars;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(float cholesterol) {
        this.cholesterol = cholesterol;
    }

    public float getSodium() {
        return sodium;
    }

    public void setSodium(float sodium) {
        this.sodium = sodium;
    }
}
