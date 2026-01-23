package com.company.repositories.interfaces;

public interface IAdminRepository {
    int addAirline(String name) throws Exception;
    int addHotel(String name, String city, int stars, double pricePerNight, int availableRooms) throws Exception;
    int addFlight(int airlineId, String flightCode, String fromCity, String toCity,
                  java.sql.Timestamp departureTime, java.sql.Timestamp arrivalTime,
                  String classType, double basePrice, int availableSeats) throws Exception;

    String listAllBookings(int limit) throws Exception;
}