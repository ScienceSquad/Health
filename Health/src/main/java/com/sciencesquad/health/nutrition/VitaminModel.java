package com.sciencesquad.health.nutrition;

import io.realm.RealmObject;

/**
 * Supplement to the NutritionModel
 * Holds all vitamins relevant to the user.
 * Stored as percentages based on a 2,000 calorie diet.
 */
public class VitaminModel extends RealmObject {

    private double vitaminA;
    private double vitaminB1;
    private double vitaminB2;
    private double vitaminB3;
    private double vitaminB5;
    private double vitaminB6;
    private double vitaminB9;
    private double vitaminB12;
    private double vitaminC;
    private double vitaminD;
    private double vitaminE;
    private double vitaminK;

    public double getVitaminA() {
        return vitaminA;
    }

    public void setVitaminA(double vitaminA) {
        this.vitaminA = vitaminA;
    }

    public double getVitaminB1() {
        return vitaminB1;
    }

    public void setVitaminB1(double vitaminB1) {
        this.vitaminB1 = vitaminB1;
    }

    public double getVitaminB2() {
        return vitaminB2;
    }

    public void setVitaminB2(double vitaminB2) {
        this.vitaminB2 = vitaminB2;
    }

    public double getVitaminB3() {
        return vitaminB3;
    }

    public void setVitaminB3(double vitaminB3) {
        this.vitaminB3 = vitaminB3;
    }

    public double getVitaminB5() {
        return vitaminB5;
    }

    public void setVitaminB5(double vitaminB5) {
        this.vitaminB5 = vitaminB5;
    }

    public double getVitaminB6() {
        return vitaminB6;
    }

    public void setVitaminB6(double vitaminB6) {
        this.vitaminB6 = vitaminB6;
    }

    public double getVitaminB9() {
        return vitaminB9;
    }

    public void setVitaminB9(double vitaminB9) {
        this.vitaminB9 = vitaminB9;
    }

    public double getVitaminB12() {
        return vitaminB12;
    }

    public void setVitaminB12(double vitaminB12) {
        this.vitaminB12 = vitaminB12;
    }

    public double getVitaminC() {
        return vitaminC;
    }

    public void setVitaminC(double vitaminC) {
        this.vitaminC = vitaminC;
    }

    public double getVitaminD() {
        return vitaminD;
    }

    public void setVitaminD(double vitaminD) {
        this.vitaminD = vitaminD;
    }

    public double getVitaminE() {
        return vitaminE;
    }

    public void setVitaminE(double vitaminE) {
        this.vitaminE = vitaminE;
    }

    public double getVitaminK() {
        return vitaminK;
    }

    public void setVitaminK(double vitaminK) {
        this.vitaminK = vitaminK;
    }
}
