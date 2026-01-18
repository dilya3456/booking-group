package com.company.controllers;

import com.company.services.BookingService;

public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    public String createBooking(int passengerId, int flightId, int hotelId, int nights, String method, Integer createdByUserId) {
        return service.createBooking(passengerId, flightId, hotelId, nights, method, createdByUserId);
    }

    public String listFlights(int limit) {
        return service.listFlights(limit);
    }

    public String listHotels(int limit) {
        return service.listHotels(limit);
    }

    public String listPassengers(int limit) {
        return service.listPassengers(limit);
    }

    public String getBookingDetails(int bookingId) {
        return service.getBookingDetails(bookingId);
    }
}
