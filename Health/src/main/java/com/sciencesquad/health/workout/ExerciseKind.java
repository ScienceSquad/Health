package com.sciencesquad.health.workout;

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
