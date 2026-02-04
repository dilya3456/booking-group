package com.company.repositories;

import com.company.data.interfaces.IDB;
import com.company.models.FlightRow;
import com.company.models.HotelRow;
import com.company.repositories.interfaces.IBookingRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

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

            ps.setInt(1, limit);try (ResultSet rs = ps.executeQuery()) {
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
    public int createPassenger(String name, String surname, boolean male, int age, String passportNumber) throws SQLException {
        String sql = """
            INSERT INTO passengers(full_name, passport_number, birth_date, nationality, created_at)
            VALUES (?, ?, (CURRENT_DATE - (? || ' years')::interval)::date, 'KZ', CURRENT_TIMESTAMP)
            RETURNING id
        """;

        String fullName = (name == null ? "" : name.trim()) + " " + (surname == null ? "" : surname.trim());

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fullName.trim());
            ps.setString(2, passportNumber == null ? null : passportNumber.trim());
            ps.setInt(3, age);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        }
    }@Override
    public int createGroupBooking(Connection con,
                                  List<Integer> passengerIds,
                                  int flightId,
                                  int hotelId,
                                  int nights,
                                  String method,
                                  Integer createdByUserId) {

        try {

            for (Integer pid : passengerIds) {
                if (!passengerExists(con, pid))
                    throw new RuntimeException("Passenger not found: " + pid);
            }

            FlightRow flight = getFlightForUpdate(con, flightId);
            if (flight == null) throw new RuntimeException("Flight not found");
            if (flight.getAvailableSeats() < passengerIds.size())
                throw new RuntimeException("Not enough seats");


            HotelRow hotel = getHotelForUpdate(con, hotelId);
            if (hotel == null) throw new RuntimeException("Hotel not found");
            if (hotel.getAvailableRooms() <= 0)
                throw new RuntimeException("No rooms available");

            double total = flight.getBasePrice() * passengerIds.size()
                    + hotel.getPricePerNight() * nights;


            String sql = """
  INSERT INTO bookings(passenger_id, flight_id, hotel_id, nights, total_price, created_by_user_id)
  VALUES (?, ?, ?, ?, ?, ?)
  RETURNING id
""";



            int bookingId;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, passengerIds.get(0)); // главный пассажир
                ps.setInt(2, flightId);
                ps.setInt(3, hotelId);
                ps.setInt(4, nights);
                ps.setDouble(5, total);
                ps.setObject(6, createdByUserId);

                ResultSet rs = ps.executeQuery();
                rs.next();
                bookingId = rs.getInt(1);
            }


            for (Integer pid : passengerIds) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO booking_travelers(booking_id, passenger_id) VALUES (?, ?)")) {
                    ps.setInt(1, bookingId);
                    ps.setInt(2, pid);
                    ps.executeUpdate();
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE flights SET available_seats = available_seats - ? WHERE id = ?")) {
                ps.setInt(1, passengerIds.size());
                ps.setInt(2, flightId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE hotels SET available_rooms = available_rooms - 1 WHERE id = ?")) {
                ps.setInt(1, hotelId);
                ps.executeUpdate();
            }


            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO payments(booking_id, amount, method) VALUES (?, ?, ?)")) {
                ps.setInt(1, bookingId);
                ps.setDouble(2, total);
                ps.setString(3, method);
                ps.executeUpdate();
            }

            return bookingId;

        } catch (Exception e) {
            throw new RuntimeException("createGroupBooking error: " + e.getMessage(), e);
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
            ps.setBigDecimal(5, BigDecimal.valueOf(totalPrice));if (createdByUserId == null) ps.setNull(6, Types.INTEGER);
            else ps.setInt(6, createdByUserId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        }
    }

    @Override
    public void insertPayment(Connection con, int bookingId, double amount, String method) throws SQLException {
        String checkSql = "SELECT 1 FROM payments WHERE booking_id = ?";
        boolean exists;

        try (PreparedStatement ps = con.prepareStatement(checkSql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                exists = rs.next();
            }
        }

        if (!exists) {
            String insertSql = "INSERT INTO payments(booking_id, amount, method, status) VALUES (?, ?, ?, 'PAID')";
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setInt(1, bookingId);
                ps.setBigDecimal(2, BigDecimal.valueOf(amount));
                ps.setString(3, method);
                ps.executeUpdate();
            }
        } else {
            String updateSql = "UPDATE payments SET amount = ?, method = ?, status = 'PAID' WHERE booking_id = ?";
            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                ps.setBigDecimal(1, BigDecimal.valueOf(amount));
                ps.setString(2, method);
                ps.setInt(3, bookingId);
                ps.executeUpdate();
            }
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
                   p.full_name AS main_passenger,
                   f.id AS flight_id, f.flight_code, f.from_city, f.to_city, f.class_type, f.base_price,
                   h.name AS hotel_name, h.city AS hotel_city, h.stars, h.price_per_night,
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

                int flightId = rs.getInt("flight_id");
                int nights = rs.getInt("nights");

                double flightBase = rs.getBigDecimal("base_price").doubleValue();
                double hotelNight = rs.getBigDecimal("price_per_night").doubleValue();TravelersInfo tinfo = fetchTravelersInfo(con, bookingId);

                double allAdultsFlight = flightBase * tinfo.count;
                double discountedFlight = flightBase * tinfo.flightCoefSum;
                double discountMoney = allAdultsFlight - discountedFlight;
                if (discountMoney < 0) discountMoney = 0;

                int discountPercent = 0;
                if (allAdultsFlight > 0) {
                    discountPercent = (int) Math.round((discountMoney / allAdultsFlight) * 100.0);
                }

                double hotelTotal = hotelNight * nights;

                StringBuilder out = new StringBuilder();
                out.append("BOOKING #").append(rs.getInt("id"))
                        .append("\nStatus: ").append(rs.getString("status"))
                        .append("\nCreated at: ").append(rs.getTimestamp("created_at"))
                        .append("\nMain passenger: ").append(rs.getString("main_passenger"))

                        .append("\n\nFlight: ").append(rs.getString("flight_code"))
                        .append(" ").append(rs.getString("from_city")).append("->").append(rs.getString("to_city"))
                        .append(" (").append(rs.getString("class_type")).append(")")
                        .append("\nFlight base price: ").append(round2(flightBase))

                        .append("\n\nHotel: ").append(rs.getString("hotel_name"))
                        .append(", ").append(rs.getString("hotel_city"))
                        .append(" ").append(rs.getInt("stars")).append("★")
                        .append("\nHotel price/night: ").append(round2(hotelNight))
                        .append("\nNights: ").append(nights)

                        .append("\n\nTravelers: ").append(tinfo.count)
                        .append(" (children=").append(tinfo.childrenCount)
                        .append(", adults=").append(tinfo.count - tinfo.childrenCount).append(")")
                        .append("\n").append(tinfo.listText)

                        .append("\nBreakdown:")
                        .append("\n- Flight (all adults): ").append(round2(allAdultsFlight))
                        .append("\n- Flight (with children discount): ").append(round2(discountedFlight))
                        .append("\n- Children discount: -").append(round2(discountMoney)).append(" (").append(discountPercent).append("%)")
                        .append("\n- Hotel total: ").append(round2(hotelTotal))
                        .append("\n\nTOTAL (saved in DB): ").append(rs.getBigDecimal("total_price"))

                        .append("\n\nPayment: ").append(rs.getString("method")).append(" / ").append(rs.getString("pay_status"));

                return out.toString();
            }
        }
    }

    private int getPassengerAgeFromBirthDate(Connection con, int passengerId) throws SQLException {
        String sql = "SELECT birth_date FROM passengers WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Passenger not found: " + passengerId);
                Date birth = rs.getDate("birth_date");
                if (birth == null) return 18;

                long ms = System.currentTimeMillis() - birth.getTime();
                long days = ms / (1000L * 60 * 60 * 24);
                int age = (int) (days / 365);
                return Math.max(age, 0);
            }
        }
    }

    private void insertBookingTravelers(Connection con, int bookingId, List<Integer> passengerIds) throws SQLException {
        String sql = "INSERT INTO booking_travelers(booking_id, passenger_id) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Integer pid : passengerIds) {
                ps.setInt(1, bookingId);
                ps.setInt(2, pid);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }private void decreaseSeatBy(Connection con, int flightId, int count) throws SQLException {
        String sql = "UPDATE flights SET available_seats = available_seats - ? WHERE id = ? AND available_seats >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, count);
            ps.setInt(2, flightId);
            ps.setInt(3, count);
            ps.executeUpdate();
        }
    }

    private static class TravelersInfo {
        int count;
        int childrenCount;
        double flightCoefSum;
        String listText;
    }

    private TravelersInfo fetchTravelersInfo(Connection con, int bookingId) throws SQLException {
        String sql = """
            SELECT p.id, p.full_name, p.birth_date
            FROM booking_travelers bt
            JOIN passengers p ON p.id = bt.passenger_id
            WHERE bt.booking_id = ?
            ORDER BY p.id
        """;

        TravelersInfo info = new TravelersInfo();
        StringBuilder sb = new StringBuilder();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int pid = rs.getInt("id");
                    String fullName = rs.getString("full_name");
                    Date birth = rs.getDate("birth_date");

                    int age = calcAgeYears(birth);
                    boolean child = age < 12;
                    double coef = child ? 0.7 : 1.0;

                    info.count++;
                    if (child) info.childrenCount++;
                    info.flightCoefSum += coef;

                    sb.append(" - ID=").append(pid)
                            .append(" | ").append(fullName == null ? "-" : fullName)
                            .append(" | age=").append(age)
                            .append(child ? " (CHILD)" : " (ADULT)")
                            .append("\n");
                }
            }
        }

        if (info.count == 0) {
            info.count = 1;
            info.childrenCount = 0;
            info.flightCoefSum = 1.0;
            sb.append(" - No travelers found in booking_travelers.\n");
        }

        info.listText = sb.toString();
        return info;
    }

    private int calcAgeYears(Date birthDate) {
        if (birthDate == null) return 18;
        long ms = System.currentTimeMillis() - birthDate.getTime();
        long days = ms / (1000L * 60 * 60 * 24);
        int age = (int) (days / 365);
        if (age < 0) age = 0;
        return age;
    }

    private double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
    @Override
    public String getSeatMap(int flightId) throws SQLException {
        String sql = """
        SELECT fs.seat_code,
               CASE WHEN bs.seat_code IS NULL THEN false ELSE true END AS occupied
        FROM flight_seats fs
        LEFT JOIN booking_seats bs
               ON bs.flight_id = fs.flight_id AND bs.seat_code = fs.seat_code
        WHERE fs.flight_id = ?
        ORDER BY
            CAST(SUBSTRING(fs.seat_code FROM 2) AS INT),
            SUBSTRING(fs.seat_code FROM 1 FOR 1);
    """;

        boolean[][] occ = new boolean[30][6]; // [row-1][A..F]
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, flightId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("seat_code"); // e.g. A12
                    boolean occupied = rs.getBoolean("occupied");

                    char letter = code.charAt(0);
                    int row = Integer.parseInt(code.substring(1)); // 1..30
                    int col = "ABCDEF".indexOf(letter); // 0..5
                    if (row >= 1 && row <= 30 && col >= 0) {
                        occ[row - 1][col] = occupied;
                    }
                }
            }
        }StringBuilder sb = new StringBuilder();
        sb.append("\n--- SEAT SELECTION ---\n");
        sb.append("Seat map (XX = occupied)\n\n");
        sb.append("    A   B   C   |   D   E   F\n");

        for (int r = 1; r <= 30; r++) {
            sb.append(String.format("%02d  ", r));
            for (int c = 0; c < 6; c++) {
                char letter = "ABCDEF".charAt(c);
                String code = "" + letter + r;
                String cell = occ[r - 1][c] ? "XX" : code;

                sb.append("[").append(cell).append("]");
                if (c == 2) sb.append(" | ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    @Override
    public boolean areSeatsFree(Connection con, int flightId, List<String> seatCodes) throws SQLException {
        if (seatCodes == null || seatCodes.isEmpty()) return true;

        String sql = """
        SELECT COUNT(*)
        FROM flight_seats
        WHERE flight_id = ?
          AND seat_code = ANY(?)
          AND is_occupied = TRUE
    """;

        Array arr = con.createArrayOf("text", seatCodes.toArray(new String[0]));
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            ps.setArray(2, arr);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 0;
            }
        }
    }

    @Override
    public void occupySeats(Connection con, int bookingId, int flightId, List<String> seatCodes) throws SQLException {
        if (seatCodes == null || seatCodes.isEmpty()) return;

        String updateSql = """
        UPDATE flight_seats
        SET is_occupied = TRUE
        WHERE flight_id = ?
          AND seat_code = ?
          AND is_occupied = FALSE
    """;

        try (PreparedStatement ps = con.prepareStatement(updateSql)) {
            for (String code : seatCodes) {
                ps.setInt(1, flightId);
                ps.setString(2, normalizeSeat(code));
                ps.addBatch();
            }
            ps.executeBatch();
        }

        String insertSql = "INSERT INTO booking_seats(booking_id, flight_id, seat_code) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(insertSql)) {
            for (String code : seatCodes) {
                ps.setInt(1, bookingId);
                ps.setInt(2, flightId);
                ps.setString(3, normalizeSeat(code));
                ps.addBatch();
            }
            ps.executeBatch();
        }

    }

    private String normalizeSeat(String code) throws SQLException {
        if (code == null) throw new SQLException("Seat code is null");
        String s = code.trim().toUpperCase();
        if (s.length() < 2 || s.length() > 3) throw new SQLException("Invalid seat code: " + code);
        char letter = s.charAt(0);
        if (letter < 'A' || letter > 'F') throw new SQLException("Invalid seat letter: " + code);
        int row;
        try {
            row = Integer.parseInt(s.substring(1));
        } catch (Exception e) {
            throw new SQLException("Invalid seat row: " + code);
        }
        if (row < 1 || row > 30) throw new SQLException("Seat row must be 1..30: " + code);
        return "" + letter + row;
    }

    private int[] parseSeat(String code) {
        try {
            String s = code.trim().toUpperCase();
            char letter = s.charAt(0);
            int row = Integer.parseInt(s.substring(1));
            if (letter < 'A' || letter > 'F') return null;
            if (row < 1 || row > 30) return null;
            int col = letter - 'A';
            int r = row - 1;
            return new int[]{r, col};
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public void insertBookingSeats(Connection con, int bookingId, List<String> seatCodes) throws SQLException {
        String sql = "INSERT INTO booking_seats(booking_id, seat_code) VALUES (?, ?)";try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (String seat : seatCodes) {
                ps.setInt(1, bookingId);
                ps.setString(2, seat);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    @Override
    public void insertBookingExtras(Connection con, int bookingId, com.company.models.ExtraSelection extras, double extrasTotal) throws SQLException {

        String details = "EXTRAS: " + extras + ", extrasTotal=" + Math.round(extrasTotal * 100.0) / 100.0;
        insertHistory(con, bookingId, "EXTRAS_ADDED", details);


    }
    public int createCategory(String name) throws SQLException {
        String sql = "INSERT INTO categories(name) VALUES (?) RETURNING id";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public String listCategories() throws SQLException {
        String sql = "SELECT id, name FROM categories ORDER BY id";
        StringBuilder sb = new StringBuilder();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sb.append("ID=").append(rs.getInt("id"))
                        .append(" | ").append(rs.getString("name"))
                        .append("\n");
            }
        }
        return sb.length() == 0 ? "No categories." : sb.toString();
    }

    public void setHotelCategory(Connection con, int hotelId, int categoryId) throws SQLException {
        String sql = "UPDATE hotels SET category_id = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setInt(2, hotelId);
            ps.executeUpdate();
        }
    }

    public String getFullBookingDescription(int bookingId) throws SQLException {
        String sql = """
        SELECT
            b.id AS booking_id,
            b.status,
            b.nights,
            b.total_price,
            b.created_at,

            p.full_name AS main_passenger,

            f.flight_code,
            f.from_city,
            f.to_city,
            f.class_type,
            f.departure_time,
            f.arrival_time,
            f.base_price,

            h.name AS hotel_name,
            h.city AS hotel_city,
            h.stars,
            h.price_per_night,

            c.name AS hotel_category,

            pay.method AS pay_method,
            pay.status AS pay_status,
            pay.amount AS pay_amount
        FROM bookings b
        JOIN passengers p ON p.id = b.passenger_id
        JOIN flights f ON f.id = b.flight_id
        JOIN hotels h ON h.id = b.hotel_id
        LEFT JOIN categories c ON c.id = h.category_id
        LEFT JOIN payments pay ON pay.booking_id = b.id
        WHERE b.id = ?
    """;

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return "Booking not found: " + bookingId;

                StringBuilder out = new StringBuilder();

                out.append("=== FULL BOOKING DESCRIPTION ===\n");
                out.append("Booking #").append(rs.getInt("booking_id")).append("\n");
                out.append("Status: ").append(rs.getString("status")).append("\n");
                out.append("Created at: ").append(rs.getTimestamp("created_at")).append("\n");
                out.append("Main passenger: ").append(rs.getString("main_passenger")).append("\n\n");out.append("--- FLIGHT ---\n");
                out.append("Code: ").append(rs.getString("flight_code")).append("\n");
                out.append("Route: ").append(rs.getString("from_city")).append(" -> ").append(rs.getString("to_city")).append("\n");
                out.append("Class: ").append(rs.getString("class_type")).append("\n");
                out.append("Departure: ").append(rs.getTimestamp("departure_time")).append("\n");
                out.append("Arrival: ").append(rs.getTimestamp("arrival_time")).append("\n");
                out.append("Base price: ").append(rs.getBigDecimal("base_price")).append("\n\n");

                out.append("--- HOTEL ---\n");
                out.append("Name: ").append(rs.getString("hotel_name")).append("\n");
                out.append("City: ").append(rs.getString("hotel_city")).append("\n");
                out.append("Stars: ").append(rs.getInt("stars")).append("★\n");
                out.append("Price/night: ").append(rs.getBigDecimal("price_per_night")).append("\n");
                String cat = rs.getString("hotel_category");
                out.append("Category: ").append(cat == null ? "-" : cat).append("\n\n");

                out.append("--- PAYMENT ---\n");
                String m = rs.getString("pay_method");
                String s = rs.getString("pay_status");
                BigDecimal a = rs.getBigDecimal("pay_amount");
                if (m == null) {
                    out.append("No payment record.\n");
                } else {
                    out.append("Method: ").append(m).append("\n");
                    out.append("Status: ").append(s).append("\n");
                    out.append("Amount: ").append(a).append("\n");
                }


                out.append("\n--- TRAVELERS (group) ---\n");
                out.append(fetchTravelersList(con, bookingId));

                return out.toString();
            }
        }
    }

    private String fetchTravelersList(Connection con, int bookingId) throws SQLException {
        String sql = """
        SELECT p.id, p.full_name, p.passport_number, p.birth_date
        FROM booking_travelers bt
        JOIN passengers p ON p.id = bt.passenger_id
        WHERE bt.booking_id = ?
        ORDER BY p.id
    """;

        StringBuilder sb = new StringBuilder();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    sb.append(count).append(") ID=").append(rs.getInt("id"))
                            .append(" | ").append(rs.getString("full_name"))
                            .append(" | passport=").append(rs.getString("passport_number"))
                            .append(" | birth=").append(rs.getDate("birth_date"))
                            .append("\n");
                }
                if (count == 0) sb.append("No travelers found (maybe single booking).\n");
            }
        }
        return sb.toString();
    }


}