package com.company.repositories;

import com.company.dataAdd recommendation module (repository, service, controller)
.IDB;
import com.company.models.FlightRow;
import com.company.repositories.interfaces.IRecommendationRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecommendationRepository implements IRecommendationRepository {

    private final IDB db;

    public RecommendationRepository(IDB db) {
        this.db = db;
    }

    @Override
    public List<FlightRow> getAllFlights() {
        List<FlightRow> flights = new ArrayList<>();

        String sql = """
            SELECT id, base_price, class_type, available_seats
            FROM flights
            WHERE available_seats > 0
        """;

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                flights.add(new FlightRow(
                        rs.getInt("id"),
                        rs.getDouble("base_price"),
                        rs.getString("class_type"),
                        rs.getInt("available_seats")
                ));
            }
        } catch (Exception e) {
            System.out.println("RecommendationRepository error: " + e.getMessage());
        }

        return flights;
    }
}

