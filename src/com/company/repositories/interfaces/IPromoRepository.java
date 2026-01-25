package com.company.repositories.interfaces;

import com.company.models.PromoCode;

import java.sql.Connection;
import java.sql.SQLException;

public interface IPromoRepository {
    PromoCode findValidPromo(Connection con, String code) throws SQLException;
    boolean wasPromoUsedForBooking(Connection con, int promoId, int bookingId) throws SQLException;
    void registerUsage(Connection con, int promoId, int bookingId) throws SQLException;

    java.math.BigDecimal getBookingTotal(Connection con, int bookingId) throws SQLException;
    void updateBookingTotal(Connection con, int bookingId, java.math.BigDecimal newTotal) throws SQLException;

    void insertHistory(Connection con, int bookingId, String action, String details) throws SQLException;
}
