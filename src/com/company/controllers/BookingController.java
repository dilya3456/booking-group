package com.company.controllers;

import com.company.controllers.interfaces.IBookingController;
import com.company.services.BookingService;

public class BookingController implements IBookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @Override
    public String createBooking(int passengerId, int flightId, int hotelId, int nights, String method, Integer createdByUserId) {
        return service.createBooking(passengerId, flightId, hotelId, nights, method, createdByUserId);
    }

    @Override
    public String listFlights(int limit) {
        return service.listFlights(limit);
    }

    @Override
    public String listHotels(int limit) {
        return service.listHotels(limit);
    }

    @Override
    public String listPassengers(int limit) {
        return service.listPassengers(limit);
    }

    @Override
    public String getBookingDetails(int bookingId) {
        return service.getBookingDetails(bookingId);
    }
}
