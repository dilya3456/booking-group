package com.company.repositories.interfaces;

import com.company.models.FlightRow;
import java.util.List;

public interface IRecommendationRepository {
    List<FlightRow> getAvailableFlights();
}

