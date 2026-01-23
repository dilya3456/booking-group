package com.company.repositories.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

public interface IReviewRepository {
    Connection getConnection() throws SQLException;

    boolean canReview(Connection con, int bookingId, int passengerId) throws SQLException;

    Integer getHotelIdByBooking(Connection con, int bookingId) throws SQLException;

    int insertReview(Connection con, int hotelId, int passengerId, int bookingId, int rating, String comment) throws SQLException;

    void refreshHotelRating(Connection con, int hotelId) throws SQLException;

    String listReviewsByHotel(int hotelId, int limit) throws SQLException;

    String listReviewsByPassenger(int passengerId, int limit) throws SQLException;
}
