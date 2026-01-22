package com.company.controllers.interfaces;

import java.util.List;

public interface IBookingController {
    String createBooking(int passengerId, int flightId, int hotelId, int nights, String method, Integer createdByUserId);

    String listFlights(int limit);
    String listHotels(int limit);
    String listPassengers(int limit);

    String getBookingDetails(int bookingId);

    int createPassenger(String name, String surname, String gender, int age, String passportNumber);

    String createGroupBooking(List<Integer> passengerIds, int flightId, int hotelId, int nights, String method, Integer createdByUserId);
    String getSeatMap(int flightId);
    String chooseSeats(int bookingId, int flightId, java.util.List<String> seatCodes);

}
