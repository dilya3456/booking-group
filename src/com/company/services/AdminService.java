package com.company.services;

import com.company.repositories.interfaces.IAdminRepository;

import java.sql.Timestamp;

public class AdminService {
    private final IAdminRepository repo;

    public AdminService(IAdminRepository repo) {
        this.repo = repo;
    }

    public String addAirline(String name) {
        try {
            int id = repo.addAirline(name);
            return "Airline added ✅ ID=" + id;
        } catch (Exception e) {
            return "Add airline failed: " + e.getMessage();
        }
    }

    public String addHotel(String name, String city, int stars, double pricePerNight, int availableRooms) {
        try {
            int id = repo.addHotel(name, city, stars, pricePerNight, availableRooms);
            return "Hotel added ✅ ID=" + id;
        } catch (Exception e) {
            return "Add hotel failed: " + e.getMessage();
        }
    }

    public String addFlight(int airlineId, String flightCode, String fromCity, String toCity,
                            Timestamp dep, Timestamp arr, String classType, double basePrice, int seats) {
        try {
            int id = repo.addFlight(airlineId, flightCode, fromCity, toCity, dep, arr, classType, basePrice, seats);
            return "Flight added ✅ ID=" + id;
        } catch (Exception e) {
            return "Add flight failed: " + e.getMessage();
        }
    }

    public String listAllBookings(int limit) {
        try {
            return repo.listAllBookings(limit);
        } catch (Exception e) {
            return "List bookings failed: " + e.getMessage();
        }
    }
}
