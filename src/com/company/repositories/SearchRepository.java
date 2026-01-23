package com.company.repositories;

import com.company.data.interfaces.IDB;
import com.company.models.Flight;
import com.company.models.Hotel;
import com.company.repositories.interfaces.ISearchRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchRepository implements ISearchRepository {
    private final IDB db;

    public SearchRepository(IDB db) {
        this.db = db;
    }

    @Override
    public List<String> getFlightsFromCities() {
        Connection con = null;

        try {
            con = db.getConnection();
            String sql = "SELECT DISTINCT from_city FROM flights";
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(sql);
            List<String> cities = new ArrayList<>();
            while (rs.next()) {
                cities.add(rs.getString("from_city"));
            }

            return cities;
        } catch (SQLException e) {
            System.out.println("sql error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<String> getFlightsToCities() {
        Connection con = null;

        try {
            con = db.getConnection();
            String sql = "SELECT DISTINCT to_city FROM flights";
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(sql);
            List<String> cities = new ArrayList<>();
            while (rs.next()) {
                cities.add(rs.getString("to_city"));
            }

            return cities;
        } catch (SQLException e) {
            System.out.println("sql error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Flight> getFlightsByFilter(String fromCity, String toCity, String fromDate, String toDate, String type, String sort) {
        List<Flight> flights = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT id, airline_id, flight_code, from_city, to_city, " +
                        "departure_time, arrival_time, base_price, class_type, available_seats " +
                        "FROM flights WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        // ---- фильтры ----
        if (fromCity != null && !fromCity.isEmpty()) {
            sql.append("AND from_city = ? ");
            params.add(fromCity);
        }

        if (toCity != null && !toCity.isEmpty()) {
            sql.append("AND to_city = ? ");
            params.add(toCity);
        }

        if (fromDate != null && !fromDate.equals("-") && !fromDate.isEmpty()) {
            sql.append("AND departure_time >= ? 00:00:00");
            params.add(Date.valueOf(fromDate));
        }

        if (toDate != null && !toDate.equals("-") && !toDate.isEmpty()) {
            sql.append("AND departure_time <= ? 23:59:59");
            params.add(Date.valueOf(toDate));
        }

        if ("1".equals(type)) {
            sql.append("AND class_type = 'ECONOMY' ");
        } else if ("2".equals(type)) {
            sql.append("AND class_type = 'BUSINESS' ");
        }

        // ---- сортировка ----
        if ("1".equals(sort)) {
            sql.append("ORDER BY base_price ASC ");
        } else if ("2".equals(sort)) {
            sql.append("ORDER BY departure_time ASC ");
        }

        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                st.setObject(i + 1, params.get(i));
            }

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                flights.add(new Flight(
                        rs.getInt("id"),
                        rs.getInt("airline_id"),
                        rs.getString("flight_code"),
                        rs.getString("from_city"),
                        rs.getString("to_city"),
                        rs.getTimestamp("departure_time"),
                        rs.getTimestamp("arrival_time"),
                        rs.getInt("base_price"),
                        rs.getString("class_type"),
                        rs.getInt("available_seats")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("SQL error: " + e.getMessage(), e);
        }

        return flights;
    }

    @Override
    public List<String> getHotelsCities() {
        Connection con = null;

        try {
            con = db.getConnection();
            String sql = "SELECT DISTINCT city FROM hotels";
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(sql);
            List<String> cities = new ArrayList<>();
            while (rs.next()) {
                cities.add(rs.getString("city"));
            }

            return cities;
        } catch (SQLException e) {
            System.out.println("sql error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Hotel> getHotelsByFilter(String city, int minStars, int maxPrice, String sort) {
        List<Hotel> hotels = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT id, name, stars, city, address, price_per_night, available_rooms FROM hotels WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        // ---- фильтры ----
        if (city != null && !city.isEmpty()) {
            sql.append("AND city = ? ");
            params.add(city);
        }

        if (minStars > 0) {
            sql.append("AND stars >= ? ");
            params.add(minStars);
        }

        if (maxPrice > 0) {
            sql.append("AND price_per_night <= ? ");
            params.add(maxPrice);
        }

        // ---- сортировка ----
        if ("1".equals(sort)) {
            sql.append("ORDER BY price_per_night ASC ");
        } else if ("2".equals(sort)) {
            sql.append("ORDER BY stars DESC ");
        }

        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql.toString())) {

            // Устанавливаем значения фильтров
            for (int i = 0; i < params.size(); i++) {
                st.setObject(i + 1, params.get(i));
            }

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                hotels.add(new Hotel(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("stars"),
                        rs.getString("city"),
                        rs.getString("address"),
                        rs.getInt("price_per_night"),
                        rs.getInt("available_rooms")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("SQL error: " + e.getMessage(), e);
        }

        return hotels;
    }
}
