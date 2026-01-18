package com.company.models;

import java.util.Date;

public class Hotel {
    private int id;
    private String name;
    private int stars;
    private String city;
    private String address;
    private int pricePerNight;
    private int availableRooms;

    public Hotel(int id, String name, int stars, String city, String address, int pricePerNight, int availableRooms) {
        this.id = id;
        this.name = name;
        this.stars = stars;
        this.city = city;
        this.address = address;
        this.pricePerNight = pricePerNight;
        this.availableRooms = availableRooms;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStars() {
        return stars;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public int getPricePerNight() {
        return pricePerNight;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id=" + id +
                ", name=" + name +
                ", stars='" + stars + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", pricePerNight=" + pricePerNight +
                ", availableRooms=" + availableRooms +
                '}';
    }
}
