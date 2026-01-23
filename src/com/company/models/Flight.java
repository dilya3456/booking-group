package com.company.models;

import java.util.Date;

public class Flight {
    private int id;
    private int airlineId;
    private String flightCode;
    private String fromCity;
    private String toCity;
    private Date departureTime;
    private Date arrivalTime;
    private int basePrice;
    private String classType;
    private int availableSeats;

    public Flight(int id, int airlineId, String flightCode, String fromCity, String toCity, Date departureTime, Date arrivalTime, int basePrice, String classType, int availableSeats) {
        this.id = id;
        this.airlineId = airlineId;
        this.flightCode = flightCode;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.basePrice = basePrice;
        this.classType = classType;
        this.availableSeats = availableSeats;
    }

    public int getId() {
        return id;
    }

    public int getAirlineId() {
        return airlineId;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public String getFromCity() {
        return fromCity;
    }

    public String getToCity() {
        return toCity;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public String getClassType() {
        return classType;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id=" + id +
                ", airlineId=" + airlineId +
                ", flightCode='" + flightCode + '\'' +
                ", fromCity='" + fromCity + '\'' +
                ", toCity='" + toCity + '\'' +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", basePrice=" + basePrice +
                ", classType='" + classType + '\'' +
                ", availableSeats=" + availableSeats +
                '}';
    }
}
