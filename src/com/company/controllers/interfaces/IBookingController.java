package com.company.controllers.interfaces;

public interface IBookingController {
    String createBooking(int passengerId, int flightId, int hotelId, int nights, String method, Integer createdByUserId);

    String listFlights(int limit);
    String listHotels(int limit);
    String listPassengers(int limit);

    String getBookingDetails(int bookingId);
}
