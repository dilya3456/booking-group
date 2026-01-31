package com.company.controllers.interfaces;

import com.company.models.ExtraSelection;
import java.util.List;

public interface IBookingController {

    String createBooking(int passengerId, int flightId, int hotelId, int nights,
                         String paymentMethod, Integer createdByUserId, ExtraSelection extras);

    int createGroupBooking(List<Integer> passengerIds, int flightId, int hotelId, int nights,
                           String method, Integer createdByUserId, ExtraSelection extras);

    int createPassenger(String name, String surname, String gender, int age, String passportNumber);

    String listFlights(int limit);
    String listHotels(int limit);
    String listPassengers(int limit);

    String getBookingDetails(int bookingId);

    String getSeatMap(int flightId);
    String chooseSeats(int bookingId, int flightId, List<String> seatCodes);
    String getFullBookingDescription(int bookingId);

}

