package com.company.repositories;

import com.company.data.interfaces.IDB;
import com.company.repositories.interfaces.IReviewRepository;

import java.sql.*;

public class ReviewRepository implements IReviewRepository {
    private final IDB db;

    public ReviewRepository(IDB db) {
        this.db = db;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return db.getConnection();
    }

    @Override
    public boolean canReview(Connection con, int bookingId, int passengerId) throws SQLException {
        String sql = """
            SELECT 1
            FROM bookings b
            WHERE b.id = ?
              AND b.passenger_id = ?
              AND b.status = 'CONFIRMED'
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public Integer getHotelIdByBooking(Connection con, int bookingId) throws SQLException {
        String sql = "SELECT hotel_id FROM bookings WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                int hotelId = rs.getInt("hotel_id");
                return rs.wasNull() ? null : hotelId;
            }
        }
    }

    @Override
    public int insertReview(Connection con, int hotelId, int passengerId, int bookingId, int rating, String comment) throws SQLException {
        String sql = """
            INSERT INTO hotel_reviews(hotel_id, passenger_id, booking_id, rating, comment)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hotelId);
            ps.setInt(2, passengerId);
            ps.setInt(3, bookingId);
            ps.setInt(4, rating);
            ps.setString(5, comment);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        }
    }

    @Override
    public void refreshHotelRating(Connection con, int hotelId) throws SQLException {
        String sql = """
            UPDATE hotels h
            SET rating_avg = x.avg_rating,
                rating_count = x.cnt
            FROM (
                SELECT hotel_id,
                       COALESCE(ROUND(AVG(rating)::numeric, 2), 0) AS avg_rating,
                       COUNT(*) AS cnt
                FROM hotel_reviews
                WHERE hotel_id = ?
                GROUP BY hotel_id
            ) x
            WHERE h.id = x.hotel_id
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hotelId);
            ps.executeUpdate();
        }
    }

    @Override
    public String listReviewsByHotel(int hotelId, int limit) throws SQLException {
        String sql = """
            SELECT r.id, r.rating, r.comment, r.created_at,
                   p.full_name
            FROM hotel_reviews r
            JOIN passengers p ON p.id = r.passenger_id
            WHERE r.hotel_id = ?
            ORDER BY r.created_at DESC
            LIMIT ?
        """;

        StringBuilder sb = new StringBuilder();
        sb.append("\n--- REVIEWS FOR HOTEL ID=").append(hotelId).append(" ---\n");

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, hotelId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    sb.append("ReviewID=").append(rs.getInt("id"))
                            .append(" | rating=").append(rs.getInt("rating"))
                            .append(" | by=").append(rs.getString("full_name"))
                            .append(" | at=").append(rs.getTimestamp("created_at"))
                            .append("\n  ").append(rs.getString("comment"))
                            .append("\n");
                }
                if (!any) sb.append("No reviews yet.\n");
            }
        }

        return sb.toString();
    }

    @Override
    public String listReviewsByPassenger(int passengerId, int limit) throws SQLException {
        String sql = """
            SELECT r.id, r.rating, r.comment, r.created_at,
                   h.name AS hotel_name, h.city
            FROM hotel_reviews r
            JOIN hotels h ON h.id = r.hotel_id
            WHERE r.passenger_id = ?
            ORDER BY r.created_at DESC
            LIMIT ?
        """;

        StringBuilder sb = new StringBuilder();
        sb.append("\n--- MY REVIEWS (passenger_id=").append(passengerId).append(") ---\n");

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, passengerId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    sb.append("ReviewID=").append(rs.getInt("id"))
                            .append(" | rating=").append(rs.getInt("rating"))
                            .append(" | hotel=").append(rs.getString("hotel_name"))
                            .append(" (").append(rs.getString("city")).append(")")
                            .append(" | at=").append(rs.getTimestamp("created_at"))
                            .append("\n  ").append(rs.getString("comment"))
                            .append("\n");
                }
                if (!any) sb.append("No reviews.\n");
            }
        }

        return sb.toString();
    }
}