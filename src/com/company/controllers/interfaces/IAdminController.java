package com.company.controllers.interfaces;

public interface IAdminController {
    String addAirline(String name);
    String addHotel(String name, String city, int stars, double pricePerNight, int availableRooms);
    String addFlight(int airlineId, String flightCode, String fromCity, String toCity,
                     String departureIso, String arrivalIso, String classType, double basePrice, int seats);

    String listAllBookings(int limit);
}