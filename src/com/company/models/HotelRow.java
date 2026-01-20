package com.company.models;

public class HotelRow {
    private final int id;
    private final double pricePerNight;
    private final int stars;
    private final int availableRooms;

    public HotelRow(int id, double pricePerNight, int stars, int availableRooms) {
        this.id = id;
        this.pricePerNight = pricePerNight;
        this.stars = stars;
        this.availableRooms = availableRooms;
    }

    public int getId() { return id; }
    public double getPricePerNight() { return pricePerNight; }
    public int getStars() { return stars; }
    public int getAvailableRooms() { return availableRooms; }
}
