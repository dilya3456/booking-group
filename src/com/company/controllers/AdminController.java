package com.company.controllers;

import com.company.services.AdminService;

import java.sql.Timestamp;

public class AdminController implements IAdminController {
    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    @Override
    public String addAirline(String name) {
        return service.addAirline(name);
    }

    @Override
    public String addHotel(String name, String city, int stars, double pricePerNight, int availableRooms) {
        return service.addHotel(name, city, stars, pricePerNight, availableRooms);
    }

    @Override
    public String addFlight(int airlineId, String flightCode, String fromCity, String toCity,
                            String departureIso, String arrivalIso, String classType, double basePrice, int seats) {

        Timestamp dep = Timestamp.valueOf(departureIso);
        Timestamp arr = Timestamp.valueOf(arrivalIso);
        return service.addFlight(airlineId, flightCode, fromCity, toCity, dep, arr, classType, basePrice, seats);
    }

    @Override
    public String listAllBookings(int limit) {
        return service.listAllBookings(limit);
    }
}