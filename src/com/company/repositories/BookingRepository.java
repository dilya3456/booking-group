package com.company.repositories;

import java.math.BigDecimal;
import com.company.data.interfaces.IDB;
import com.company.models.FlightRow;
import com.company.models.HotelRow;
import com.company.repositories.interfaces.IBookingRepository;

import java.sql.*;

public class BookingRepository implements IBookingRepository {
    private final IDB db;

    public BookingRepository(IDB db) {
        this.db = db;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return db.getConnection();
    }

    @Override
    public String listFlights(int limit) throws SQLException {
        String sql = """
            SELECT f.id, f.flight_code, a.name AS airline, f.from_city, f.to_city,
                   f.departure_time, f.arrival_time, f.class_type, f.base_price, f.available_seats
            FROM flights f
            JOIN airlines a ON a.id = f.airline_id
            ORDER BY f.departure_time
            LIMIT ?
        """;

        StringBuilder sb = new StringBuilder();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sb.append("ID=").append(rs.getInt("id"))
                            .append(" | ").append(rs.getString("flight_code"))
                            .append(" | ").append(rs.getString("airline"))
                            .append(" | ").append(rs.getString("from_city")).append("->").append(rs.getString("to_city"))
                            .append(" | dep=").append(rs.getTimestamp("departure_time"))
                            .append(" | arr=").append(rs.getTimestamp("arrival_time"))
                            .append(" | class=").append(rs.getString("class_type"))
                            .append(" | price=").append(rs.getBigDecimal("base_price"))
                            .append(" | seats=").append(rs.getInt("available_seats"))
                            .append("\n");
                }
            }
        }

        return sb.length() == 0 ? "No flights found." : sb.toString();
    }

    @Override
    public String listHotels(int limit) throws SQLException {
        String sql = """
            SELECT id, name, city, stars, price_per_night, available_rooms
            FROM hotels
            ORDER BY city, stars DESC, price_per_night
            LIMIT ?
        """;

        StringBuilder sb = new StringBuilder();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sb.append("ID=").append(rs.getInt("id"))
                            .append(" | ").append(rs.getString("name"))
                            .append(" | ").append(rs.getString("city"))
                            .append(" | ").append(rs.getInt("stars")).append("★")
                            .append(" | night=").append(rs.getBigDecimal("price_per_night"))
                            .append(" | rooms=").append(rs.getInt("available_rooms"))
                            .append("\n");
                }
            }
        }

        return sb.length() == 0 ? "No hotels found." : sb.toString();
    }

    @Override
    public String listPassengers(int limit) throws SQLException {
        String sql = """
            SELECT p.id, p.full_name, p.passport_number, p.birth_date, p.nationality,
                   COALESCE(l.discount_percent, 0) AS discount_percent
            FROM passengers p
            LEFT JOIN loyalty_accounts l ON l.passenger_id = p.id
            ORDER BY p.id
            LIMIT ?
        """;

        StringBuilder sb = new StringBuilder();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sb.append("ID=").append(rs.getInt("id"))
                            .append(" | ").append(rs.getString("full_name"))
                            .append(" | passport=").append(rs.getString("passport_number"))
                            .append(" | birth=").append(rs.getDate("birth_date"))
                            .append(" | ").append(rs.getString("nationality"))
                            .append(" | discount=").append(rs.getInt("discount_percent")).append("%")
                            .append("\n");
                }
            }
        }

        return sb.length() == 0 ? "No passengers found." : sb.toString();
    }

    @Override
    public FlightRow getFlightForUpdate(Connection con, int flightId) throws SQLException {
        String sql = """
            SELECT id, base_price, class_type, available_seats
            FROM flights
            WHERE id = ?
            FOR UPDATE
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, flightId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new FlightRow(
                        rs.getInt("id"),
                        rs.getDouble("base_price"),
                        rs.getString("class_type"),
                        rs.getInt("available_seats")
                );
            }
        }
    }

    @Override
    public HotelRow getHotelForUpdate(Connection con, int hotelId) throws SQLException {
        String sql = """
            SELECT id, price_per_night, stars, available_rooms
            FROM hotels
            WHERE id = ?
            FOR UPDATE
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hotelId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new HotelRow(
                        rs.getInt("id"),
                        rs.getDouble("price_per_night"),
                        rs.getInt("stars"),
                        rs.getInt("available_rooms")
                );
            }
        }
    }

    @Override
    public boolean passengerExists(Connection con, int passengerId) throws SQLException {
        String sql = "SELECT 1 FROM passengers WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public int getPassengerDiscount(Connection con, int passengerId) throws SQLException {
        String sql = "SELECT discount_percent FROM loyalty_accounts WHERE passenger_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("discount_percent") : 0;
            }
        }
    }

    @Override
    public int insertBooking(Connection con, int passengerId, int flightId, int hotelId, int nights,
                             double totalPrice, Integer createdByUserId) throws SQLException {
        String sql = """
            INSERT INTO bookings(passenger_id, flight_id, hotel_id, nights, total_price, status, created_by_user_id)
            VALUES (?, ?, ?, ?, ?, 'CONFIRMED', ?)
            RETURNING id
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            ps.setInt(2, flightId);
            ps.setInt(3, hotelId);
            ps.setInt(4, nights);
            ps.setBigDecimal(5, BigDecimal.valueOf(totalPrice));

            if (createdByUserId == null) ps.setNull(6, Types.INTEGER);
            else ps.setInt(6, createdByUserId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        }
    }

    @Override
    public void insertPayment(Connection con, int bookingId, double amount, String method) throws SQLException {
        String sql = """
            INSERT INTO payments(booking_id, amount, method, status)
            VALUES (?, ?, ?, 'PAID')
            ON CONFLICT (booking_id) DO UPDATE
            SET amount = EXCLUDED.amount,
                method = EXCLUDED.method,
                status = 'PAID',
                paid_at = CURRENT_TIMESTAMP
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setBigDecimal(2, BigDecimal.valueOf(amount));
            ps.setString(3, method);
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

    @Override
    public void decreaseSeat(Connection con, int flightId) throws SQLException {
        String sql = "UPDATE flights SET available_seats = available_seats - 1 WHERE id = ? AND available_seats > 0";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            ps.executeUpdate();
        }
    }

    @Override
    public void decreaseRoom(Connection con, int hotelId) throws SQLException {
        String sql = "UPDATE hotels SET available_rooms = available_rooms - 1 WHERE id = ? AND available_rooms > 0";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hotelId);
            ps.executeUpdate();
        }
    }

    @Override
    public String getBookingDetails(int bookingId) throws SQLException {
        String sql = """
            SELECT b.id, b.status, b.nights, b.total_price, b.created_at,
                   p.full_name,
                   f.flight_code, f.from_city, f.to_city, f.class_type,
                   h.name AS hotel_name, h.city AS hotel_city, h.stars,
                   pay.method, pay.status AS pay_status
            FROM bookings b
            JOIN passengers p ON p.id = b.passenger_id
            JOIN flights f ON f.id = b.flight_id
            JOIN hotels h ON h.id = b.hotel_id
            LEFT JOIN payments pay ON pay.booking_id = b.id
            WHERE b.id = ?
        """;

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return "Booking not found.";

                return "BOOKING #" + rs.getInt("id")
                        + "\nStatus: " + rs.getString("status")
                        + "\nPassenger: " + rs.getString("full_name")
                        + "\nFlight: " + rs.getString("flight_code") + " " + rs.getString("from_city") + "->" + rs.getString("to_city")
                        + " (" + rs.getString("class_type") + ")"
                        + "\nHotel: " + rs.getString("hotel_name") + ", " + rs.getString("hotel_city") + " " + rs.getInt("stars") + "★"
                        + "\nNights: " + rs.getInt("nights")
                        + "\nTotal: " + rs.getBigDecimal("total_price")
                        + "\nPayment: " + rs.getString("method") + " / " + rs.getString("pay_status")
                        + "\nCreated at: " + rs.getTimestamp("created_at");
            }
        }
    }
}
