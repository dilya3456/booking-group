package com.company.controllers;

import com.company.controllers.interfaces.IBookingController;
import com.company.models.ExtraSelection;
import com.company.services.BookingService;

import java.util.List;

public class BookingController implements IBookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @Override
    public String createBooking(int passengerId, int flightId, int hotelId, int nights,
                                String paymentMethod, Integer createdByUserId, ExtraSelection extras) {
        return service.createBooking(passengerId, flightId, hotelId, nights, paymentMethod, createdByUserId, extras);
    }

    @Override
    public int createGroupBooking(List<Integer> passengerIds, int flightId, int hotelId, int nights,
                                  String method, Integer createdByUserId, ExtraSelection extras) {
        return service.createGroupBooking(passengerIds, flightId, hotelId, nights, method, createdByUserId, extras);
    }

    @Override
    public int createPassenger(String name, String surname, String gender, int age, String passportNumber) {
        return service.createPassenger(name, surname, gender, age, passportNumber);
    }

    @Override public String listFlights(int limit) { return service.listFlights(limit); }
    @Override public String listHotels(int limit) { return service.listHotels(limit); }
    @Override public String listPassengers(int limit) { return service.listPassengers(limit); }
    @Override public String getBookingDetails(int bookingId) { return service.getBookingDetails(bookingId); }
    @Override public String getSeatMap(int flightId) { return service.getSeatMap(flightId); }
    @Override public String chooseSeats(int bookingId, int flightId, List<String> seatCodes) {
        return service.chooseSeats(bookingId, flightId, seatCodes);
    }
    @Override
    public String getFullBookingDescription(int bookingId) {
        return service.getFullBookingDescription(bookingId);
    }

}
