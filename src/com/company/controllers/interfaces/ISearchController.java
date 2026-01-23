package com.company.controllers.interfaces;

import java.util.Date;

public interface ISearchController {
    String getFlightsFromCities();

    String getFlightsToCities();

    String getFlightsByFilter(String fromCity, String toCity, String fromDate, String toDate, String type, String sort);

    String getHotelsCities();

    String getHotelsByFilter(String city, int minStars, int maxPrice, String sort);
}
