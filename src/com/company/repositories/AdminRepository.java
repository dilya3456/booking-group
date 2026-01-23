
        package com.company.repositories;

import com.company.data.interfaces.IDB;
import com.company.repositories.interfaces.IAdminRepository;

import java.sql.*;

public class AdminRepository implements IAdminRepository {
    private final IDB db;

    public AdminRepository(IDB db) {
        this.db = db;
    }

    @Override
    public int addAirline(String name) throws Exception {
        String sql = "INSERT INTO airlines(name) VALUES (?) RETURNING id";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        }
    }

    @Override
    public int addHotel(String name, String city, int stars, double pricePerNight, int availableRooms) throws Exception {
        String sql = """
            INSERT INTO hotels(name, city, stars, price_per_night, available_rooms)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
        """;
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, city);
            ps.setInt(3, stars);
            ps.setBigDecimal(4, java.math.BigDecimal.valueOf(pricePerNight));
            ps.setInt(5, availableRooms);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        }
    }

    @Override
    public int addFlight(int airlineId, String flightCode, String fromCity, String toCity,
                         Timestamp departureTime, Timestamp arrivalTime,
                         String classType, double basePrice, int availableSeats) throws Exception {
        String sql = """
            INSERT INTO flights(airline_id, flight_code, from_city, to_city, departure_time, arrival_time,
                                class_type, base_price, available_seats)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """;
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, airlineId);
            ps.setString(2, flightCode);
            ps.setString(3, fromCity);
            ps.setString(4, toCity);
            ps.setTimestamp(5, departureTime);
            ps.setTimestamp(6, arrivalTime);
            ps.setString(7, classType);
            ps.setBigDecimal(8, java.math.BigDecimal.valueOf(basePrice));
            ps.setInt(9, availableSeats);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        }
    }

    @Override
    public String listAllBookings(int limit) throws Exception {
        String sql = """
            SELECT b.id, b.status, b.total_price, b.created_at,
                   p.full_name AS passenger,
                   f.flight_code, f.from_city, f.to_city,
                   h.name AS hotel_name, h.city AS hotel_city
            FROM bookings b
            JOIN passengers p ON p.id = b.passenger_id
            JOIN flights f ON f.id = b.flight_id
            JOIN hotels h ON h.id = b.hotel_id
            ORDER BY b.created_at DESC
            LIMIT ?
        """;

        StringBuilder sb = new StringBuilder();
        sb.append("\n========== ALL BOOKINGS ==========\n");

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sb.append("ID=").append(rs.getInt("id"))
                            .append(" | ").append(rs.getString("status"))
                            .append(" | total=").append(rs.getBigDecimal("total_price"))
                            .append(" | passenger=").append(rs.getString("passenger"))
                            .append(" | flight=").append(rs.

                    getString("flight_code"))
                      .append(" ").append(rs.getString("from_city")).append("->").append(rs.getString("to_city"))
                            .append(" | hotel=").append(rs.getString("hotel_name"))
                            .append(" (").append(rs.getString("hotel_city")).append(")")
                            .append(" | created=").append(rs.getTimestamp("created_at"))
                            .append("\n");
                }
            }
        }

        return sb.toString();
    }
}