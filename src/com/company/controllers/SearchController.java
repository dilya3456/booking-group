package com.company.controllers;

import com.company.controllers.interfaces.ISearchController;
import com.company.models.Flight;
import com.company.models.Hotel;
import com.company.repositories.interfaces.ISearchRepository;

import java.util.List;

public class SearchController implements ISearchController {
    private final ISearchRepository repo;

    public SearchController(ISearchRepository repo) { // Dependency Injection
        this.repo = repo;
    }

    public String getFlightsFromCities() {
        List<String> cities = repo.getFlightsFromCities();

        StringBuilder response = new StringBuilder();
        for (String city : cities) {
            response.append(city).append("; ");
        }

        return response.toString();
    }

    public String getFlightsToCities() {
        List<String> cities = repo.getFlightsToCities();

        StringBuilder response = new StringBuilder();
        for (String city : cities) {
            response.append(city).append("; ");
        }

        return response.toString();
    }

    public String getFlightsByFilter(String fromCity, String toCity, String fromDate, String toDate, String type, String sort) {
        List<Flight> flights = repo.getFlightsByFilter(fromCity, toCity, fromDate, toDate, type, sort);

        StringBuilder response = new StringBuilder();
        for (Flight flight : flights) {
            response.append(flight).append("\n");
        }

        return response.toString();
    }

    public String getHotelsCities() {
        List<String> cities = repo.getHotelsCities();

        StringBuilder response = new StringBuilder();
        for (String city : cities) {
            response.append(city).append("; ");
        }

        return response.toString();
    }

    public String getHotelsByFilter(String city, int minStars, int maxPrice, String sort) {
        List<Hotel> hotels = repo.getHotelsByFilter(city, minStars, maxPrice, sort);

        StringBuilder response = new StringBuilder();
        for (Hotel hotel : hotels) {
            response.append(hotel).append("\n");
        }

        return response.toString();
    }
}
