package com.company.controllers;

import com.company.controllers.interfaces.IAdminController;
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
                            String departureTime, String arrivalTime, String classType,
                            double basePrice, int availableSeats) {

        try {
            Timestamp dep = Timestamp.valueOf(departureTime);
            Timestamp arr = Timestamp.valueOf(arrivalTime);

            return service.addFlight(
                    airlineId, flightCode, fromCity, toCity,
                    dep, arr, classType, basePrice, availableSeats
            );

        } catch (Exception e) {
            return "Invalid datetime format. Use: yyyy-mm-dd hh:mm:ss";
        }
    }

    @Override
    public String listAllBookings() {
        return service.listAllBookings();
    }

    @Override
    public String revenueReports() {
        return service.revenueReports();
    }
    @Override
    public String listCategories() {
        return service.listCategories();
    }

    @Override
    public String createCategory(String name) {
        return service.createCategory(name);
    }

    @Override
    public String setHotelCategory(int hotelId, int categoryId) {
        return service.setHotelCategory(hotelId, categoryId);
    }

}