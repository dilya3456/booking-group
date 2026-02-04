package com.company.repositories.interfaces;

import java.sql.SQLException;
import java.sql.Timestamp;

public interface IAdminRepository {

    int addAirline(String name) throws SQLException;

    int addHotel(String name, String city, int stars, double pricePerNight, int availableRooms) throws SQLException;

    int addFlight(int airlineId, String flightCode, String fromCity, String toCity,
                  Timestamp departureTime, Timestamp arrivalTime, String classType,
                  double basePrice, int availableSeats) throws SQLException;

    String listAllBookings() throws SQLException;

    String revenueReports() throws SQLException;
    int createCategory(String name) throws Exception;

    String listCategories() throws Exception;

    void setHotelCategory(int hotelId, int categoryId) throws Exception;

}