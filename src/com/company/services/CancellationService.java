package com.company.services;

import com.company.data.interfaces.IDB;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class CancellationService {
    private final IDB db;

    public CancellationService(IDB db) {
        this.db = db;
    }

    public String cancelBooking(int bookingId) {
        Connection con = null;

        try {
            con = db.getConnection();
            con.setAutoCommit(false);

            String loadSql = """
                SELECT b.id, b.status, b.total_price, b.flight_id, b.hotel_id,
                       f.departure_time
                FROM bookings b
                JOIN flights f ON f.id = b.flight_id
                WHERE b.id = ?
                FOR UPDATE
            """;

            int flightId;
            int hotelId;
            String status;
            double totalPrice;
            LocalDateTime departureTime;

            try (PreparedStatement ps = con.prepareStatement(loadSql)) {
                ps.setInt(1, bookingId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        return "Booking not found.";
                    }
                    status = rs.getString("status");
                    totalPrice = rs.getDouble("total_price");
                    flightId = rs.getInt("flight_id");
                    hotelId = rs.getInt("hotel_id");

                    Timestamp depTs = rs.getTimestamp("departure_time");
                    if (depTs == null) {
                        con.rollback();
                        return "Flight departure_time is missing. Set it in flights table.";
                    }
                    departureTime = depTs.toLocalDateTime();
                }
            }

            if (!"CONFIRMED".equalsIgnoreCase(status)) {
                con.rollback();
                return "Only CONFIRMED bookings can be cancelled.";
            }

            LocalDateTime now = LocalDateTime.now();
            long hoursLeft = Duration.between(now, departureTime).toHours();

            double refundRate = (hoursLeft >= 48) ? 0.90 : 0.50;
            double refundAmount = round2(totalPrice * refundRate);


            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE bookings SET status='CANCELLED' WHERE id=?")) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }


            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO cancellations(booking_id, refund_amount) VALUES (?, ?) ON CONFLICT (booking_id) DO NOTHING")) {
                ps.setInt(1, bookingId);
                ps.setDouble(2, refundAmount);
                ps.executeUpdate();
            }


            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE payments SET status='REFUNDED' WHERE booking_id=?")) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }


            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE flights SET available_seats = available_seats + 1 WHERE id=?")) {
                ps.setInt(1, flightId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE hotels SET available_rooms = available_rooms + 1 WHERE id=?")) {
                ps.setInt(1, hotelId);
                ps.executeUpdate();
            }


            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO booking_history(booking_id, action) VALUES (?, 'CANCELLED')")) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }

            con.commit();
            return "Booking cancelled. Refund: " + refundAmount;
        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (SQLException ignore) {}
            return "Cancellation failed: " + e.getMessage();
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (SQLException ignore) {}
        }
    }

    private double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
}
