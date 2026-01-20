package com.company.models;

public class FlightRow {
    private final int id;
    private final double basePrice;
    private final String classType;
    private final int availableSeats;

    public FlightRow(int id, double basePrice, String classType, int availableSeats) {
        this.id = id;
        this.basePrice = basePrice;
        this.classType = classType;
        this.availableSeats = availableSeats;
    }

    public int getId() { return id; }
    public double getBasePrice() { return basePrice; }
    public String getClassType() { return classType; }
    public int getAvailableSeats() { return availableSeats; }
}

