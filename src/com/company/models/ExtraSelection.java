package com.company.models;

public class ExtraSelection {
    public enum MealType { NONE, STANDARD, VEG, KIDS }

    private int baggageKg;
    private MealType mealType;
    private boolean insurance;
    private boolean priority;

    public ExtraSelection(int baggageKg, MealType mealType, boolean insurance, boolean priority) {
        this.baggageKg = baggageKg;
        this.mealType = mealType == null ? MealType.NONE : mealType;
        this.insurance = insurance;
        this.priority = priority;
    }

    public static ExtraSelection basic() {
        return new ExtraSelection(0, MealType.NONE, false, false);
    }

    public static ExtraSelection comfort() {

        return new ExtraSelection(20, MealType.STANDARD, false, false);
    }

    public static ExtraSelection premium() {

        return new ExtraSelection(30, MealType.STANDARD, true, true);
    }

    public int getBaggageKg() { return baggageKg; }
    public MealType getMealType() { return mealType; }
    public boolean isInsurance() { return insurance; }
    public boolean isPriority() { return priority; }

    @Override
    public String toString() {
        return "baggageKg=" + baggageKg +
                ", meal=" + mealType +
                ", insurance=" + insurance +
                ", priority=" + priority;
    }
}