package com.company.repositories;

import com.company.data.interfaces.IDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportRepository {
    private final IDB db;

    public ReportRepository(IDB db) {
        this.db = db;
    }

    public String revenueByAirline() {
        String sql = """
            SELECT a.name AS airline,
                   COUNT(b.id) AS bookings_count,
                   COALESCE(SUM(b.total_price), 0) AS revenue
            FROM bookings b
            JOIN flights f ON f.id = b.flight_id
            JOIN airlines a ON a.id = f.airline_id
            WHERE b.status = 'CONFIRMED'
            GROUP BY a.name
            ORDER BY revenue DESC;
        """;

        return queryToString(sql, "Revenue by Airline");
    }

    public String topRoutes() {
        String sql = """
            SELECT f.from_city, f.to_city,
                   COUNT(b.id) AS bookings_count
            FROM bookings b
            JOIN flights f ON f.id = b.flight_id
            GROUP BY f.from_city, f.to_city
            ORDER BY bookings_count DESC
            LIMIT 10;
        """;

        return queryToString(sql, "Top Routes (Top 10)");
    }

    public String revenueByHotelCity() {
        String sql = """
            SELECT h.city AS hotel_city,
                   COUNT(b.id) AS bookings_count,
                   COALESCE(SUM(b.total_price), 0) AS revenue
            FROM bookings b
            JOIN hotels h ON h.id = b.hotel_id
            WHERE b.status = 'CONFIRMED'
            GROUP BY h.city
            ORDER BY revenue DESC;
        """;

        return queryToString(sql, "Revenue by Hotel City");
    }

    public String cancellationStats() {
        String sql = """
            SELECT
                COUNT(*) AS cancelled_count,
                COALESCE(SUM(c.refund_amount), 0) AS total_refund
            FROM cancellations c;
        """;

        return queryToString(sql, "Cancellation Statistics");
    }

    public String averageStayByHotelCity() {
        String sql = """
            SELECT h.city AS hotel_city,
                   ROUND(AVG(b.nights)::numeric, 2) AS avg_nights,
                   COUNT(*) AS bookings_count
            FROM bookings b
            JOIN hotels h ON h.id = b.hotel_id
            GROUP BY h.city
            ORDER BY avg_nights DESC;
        """;

        return queryToString(sql, "Average Stay by Hotel City");
    }

    // helper: executes any query and formats output
    private String queryToString(String sql, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== ").append(title).append(" ==========\n");

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int cols = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    String colName = rs.getMetaData().getColumnLabel(i);
                    String value = rs.getString(i);
                    sb.append(colName).append("=").append(value).append("  ");
                }
                sb.append("\n");
            }

        } catch (Exception e) {
            return "Report error: " + e.getMessage();
        }

        return sb.toString();
    }
}