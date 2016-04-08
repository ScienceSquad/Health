package com.sciencesquad.health.overview;

import android.support.annotation.NonNull;

import java.util.Date;

import io.realm.RealmObject;

public class OverviewModel extends RealmObject {

    private Date date;

    private int overviewCoefficient;
    private int nutritionCoefficient;
    private int prescriptionCoefficient;
    private int runCoefficient;
    private int sleepCoefficient;
    private int stepCoefficient;
    private int workoutCoefficient;

    public int getOverviewCoefficient() {
        return overviewCoefficient;
    }

    public void setOverviewCoefficient(int overviewCoefficient) {
        this.overviewCoefficient = overviewCoefficient;
    }

    public int getNutritionCoefficient() {
        return nutritionCoefficient;
    }

    public void setNutritionCoefficient(int nutritionCoefficient) {
        this.nutritionCoefficient = nutritionCoefficient;
    }

    public int getPrescriptionCoefficient() {
        return prescriptionCoefficient;
    }

    public void setPrescriptionCoefficient(int prescriptionCoefficient) {
        this.prescriptionCoefficient = prescriptionCoefficient;
    }

    public int getRunCoefficient() {
        return runCoefficient;
    }

    public void setRunCoefficient(int runCoefficient) {
        this.runCoefficient = runCoefficient;
    }

    public int getSleepCoefficient() {
        return sleepCoefficient;
    }

    public void setSleepCoefficient(int sleepCoefficient) {
        this.sleepCoefficient = sleepCoefficient;
    }

    public int getStepCoefficient() {
        return runCoefficient;
    }

    public void setStepCoefficient(int stepoefficient) {
        this.stepCoefficient = stepCoefficient;
    }

    public int getWorkoutCoefficient() {
        return workoutCoefficient;
    }

    public void setWorkoutCoefficient(int workoutCoefficient) {
        this.workoutCoefficient = workoutCoefficient;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }
}
