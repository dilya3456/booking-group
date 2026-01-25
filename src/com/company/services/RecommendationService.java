package com.company.services;

import com.company.models.FlightRow;
import java.util.*;

public class RecommendationService {

    public static class Result {
        public FlightRow best;
        public List<String> reasons;

        public Result(FlightRow best, List<String> reasons) {
            this.best = best;
            this.reasons = reasons;
        }
    }

    public Result recommend(List<FlightRow> flights) {
        if (flights.isEmpty()) {
            return new Result(null, List.of("No flights available"));
        }

        double minPrice = flights.stream()
                .mapToDouble(FlightRow::getBasePrice)
                .min().orElse(0);

        int maxSeats = flights.stream()
                .mapToInt(FlightRow::getAvailableSeats)
                .max().orElse(0);

        FlightRow best = flights.stream()
                .min(Comparator.comparingDouble(f ->
                        (f.getBasePrice() / minPrice) +
                                ((double) maxSeats / f.getAvailableSeats())
                ))
                .orElse(flights.get(0));

        List<String> reasons = new ArrayList<>();

        if (best.getBasePrice() == minPrice)
            reasons.add("✔ Cheapest option");

        if (best.getAvailableSeats() == maxSeats)
            reasons.add("✔ Most available seats");

        if ("Business".equalsIgnoreCase(best.getClassType()))
            reasons.add("✔ Business class");

        if (reasons.isEmpty())
            reasons.add("✔ Best balanced option");

        return new Result(best, reasons);
    }
}

