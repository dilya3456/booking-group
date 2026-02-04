package com.company.controllers.interfaces;

public interface IAdminController {
    String addAirline(String name);

    String addHotel(String name, String city, int stars, double pricePerNight, int availableRooms);

    String addFlight(int airlineId, String flightCode, String fromCity, String toCity,
                     String departureTime, String arrivalTime, String classType,
                     double basePrice, int availableSeats);

    String listAllBookings();

    String revenueReports();

    String listCategories();

    String createCategory(String name);

    String setHotelCategory(int hotelId, int categoryId);

}