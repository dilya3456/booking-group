package com.company.repositories.interfaces;

import com.company.models.Flight;
import com.company.models.Hotel;

import java.util.List;

public interface ISearchRepository {
    List<String> getFlightsFromCities();

    List<String> getFlightsToCities();

    List<Flight> getFlightsByFilter(String fromCity, String toCity, String fromDate, String toDate, String type, String sort);

    List<String> getHotelsCities();

    List<Hotel> getHotelsByFilter(String city, int minStars, int maxPrice, String sort);
}
