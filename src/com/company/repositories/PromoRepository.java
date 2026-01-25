package com.company.repositories;

import com.company.data.interfaces.IDB;
import com.company.models.PromoCode;
import com.company.repositories.interfaces.IPromoRepository;

import java.math.BigDecimal;
import java.sql.*;

public class PromoRepository implements IPromoRepository {
    private final IDB db;

    public PromoRepository(IDB db) {
        this.db = db;
    }

    @Override
    public PromoCode findValidPromo(Connection con, String code) throws SQLException {
        String sql = """
            SELECT pc.id, pc.code, pc.discount_percent, pc.discount_amount, pc.active, pc.expires_at, pc.usage_limit,
                   COALESCE(u.used_count, 0) AS used_count
            FROM promo_codes pc
            LEFT JOIN (
                SELECT promo_id, COUNT(*) AS used_count
                FROM promo_usages
                GROUP BY promo_id
            ) u ON u.promo_id = pc.id
            WHERE UPPER(pc.code) = UPPER(?)
            LIMIT 1
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code == null ? "" : code.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new PromoCode(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getInt("discount_percent"),
                        rs.getBigDecimal("discount_amount"),
                        rs.getBoolean("active"),
                        rs.getTimestamp("expires_at"),
                        (Integer) rs.getObject("usage_limit"),
                        rs.getInt("used_count")
                );
            }
        }
    }

    @Override
    public boolean wasPromoUsedForBooking(Connection con, int promoId, int bookingId) throws SQLException {
        String sql = "SELECT 1 FROM promo_usages WHERE promo_id = ? AND booking_id = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, promoId);
            ps.setInt(2, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void registerUsage(Connection con, int promoId, int bookingId) throws SQLException {
        String sql = """
            INSERT INTO promo_usages(promo_id, booking_id)
            VALUES (?, ?)
            ON CONFLICT (promo_id, booking_id) DO NOTHING
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, promoId);
            ps.setInt(2, bookingId);
            ps.executeUpdate();
        }
    }

    @Override
    public BigDecimal getBookingTotal(Connection con, int bookingId) throws SQLException {
        String sql = "SELECT total_price FROM bookings WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Booking not found: " + bookingId);
                return rs.getBigDecimal("total_price");
            }
        }
    }

    @Override
    public void updateBookingTotal(Connection con, int bookingId, BigDecimal newTotal) throws SQLException {
        String sql = "UPDATE bookings SET total_price = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, newTotal);
            ps.setInt(2, bookingId);
            ps.executeUpdate();
        }
    }

    @Override
    public void insertHistory(Connection con, int bookingId, String action, String details) throws SQLException {
        String sql = "INSERT INTO booking_history(booking_id, action, details) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setString(2, action);
            ps.setString(3, details);
            ps.executeUpdate();
        }
    }
}