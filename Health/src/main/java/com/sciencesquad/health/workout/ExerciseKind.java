package com.sciencesquad.health.workout;

/**
 * Created by mrjohnson on 3/3/16.
 */
public enum ExerciseKind {
    STRENGTH("Strength"),
    CARDIO("Cardio");

    private String theKind;

    ExerciseKind(String aKind) {
        theKind = aKind;
    }

    @Override public String toString() {
        return theKind;
    }


}
