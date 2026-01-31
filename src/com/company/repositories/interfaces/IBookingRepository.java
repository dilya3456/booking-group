package com.company.repositories.interfaces;

import com.company.models.FlightRow;
import com.company.models.HotelRow;
import com.company.models.ExtraSelection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IBookingRepository {
    Connection getConnection() throws SQLException;

    String listFlights(int limit) throws SQLException;

    String listHotels(int limit) throws SQLException;

    String listPassengers(int limit) throws SQLException;

    FlightRow getFlightForUpdate(Connection con, int flightId) throws SQLException;

    HotelRow getHotelForUpdate(Connection con, int hotelId) throws SQLException;

    boolean passengerExists(Connection con, int passengerId) throws SQLException;

    int getPassengerDiscount(Connection con, int passengerId) throws SQLException;

    int createPassenger(String name, String surname, boolean male, int age, String passportNumber) throws SQLException;

    int createGroupBooking(Connection con, List<Integer> passengerIds, int flightId, int hotelId,
                           int nights, String method, Integer createdByUserId);


    int insertBooking(Connection con, int passengerId, int flightId, int hotelId, int nights,
                      double totalPrice, Integer createdByUserId) throws SQLException;

    void insertPayment(Connection con, int bookingId, double amount, String method) throws SQLException;

    void insertHistory(Connection con, int bookingId, String action, String details) throws SQLException;

    void decreaseSeat(Connection con, int flightId) throws SQLException;

    void decreaseRoom(Connection con, int hotelId) throws SQLException;

    String getBookingDetails(int bookingId) throws SQLException;


    String getSeatMap(int flightId) throws SQLException;

    boolean areSeatsFree(Connection con, int flightId, List<String> seatCodes) throws SQLException;

    void occupySeats(Connection con, int bookingId, int flightId, List<String> seatCodes) throws SQLException;


    void insertBookingExtras(Connection con, int bookingId, ExtraSelection extras, double extrasTotal) throws SQLException;

    void insertBookingSeats(Connection con, int bookingId, List<String> seatCodes) throws SQLException;

    String getFullBookingDescription(int bookingId) throws Exception;

}
