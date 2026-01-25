package com.company.services;

import com.company.models.FlightRow;

import java.util.*;

public class RecommendationService {

    public static class Result {
        private final FlightRow best;
        private final List<String> reasons;

        public Result(FlightRow best, List<String> reasons) {
            this.best = best;
            this.reasons = reasons;
        }

        public FlightRow getBest() { return best; }
        public List<String> getReasons() { return reasons; }
    }

    public Result recommend(List<FlightRow> flights) {
        if (flights == null || flights.isEmpty()) {
            return new Result(null, List.of("No flights available"));
        }

        double minPrice = flights.stream()
                .mapToDouble(FlightRow::getBasePrice)
                .min().orElse(0);

        int maxSeats = flights.stream()
                .mapToInt(FlightRow::getAvailableSeats)
                .max().orElse(0);

        // Score: чем дешевле и больше мест — тем лучше
        FlightRow best = flights.stream()
                .min(Comparator.comparingDouble(f -> score(f, minPrice, maxSeats)))
                .orElse(flights.get(0));

        List<String> reasons = new ArrayList<>();

        if (best.getBasePrice() == minPrice) {
            reasons.add("✔ Cheapest option");
        } else if (best.getBasePrice() <= minPrice * 1.10) {
            reasons.add("✔ Price close to cheapest (within 10%)");
        }

        if (best.getAvailableSeats() == maxSeats) {
            reasons.add("✔ Most available seats");
        }

        if ("Business".equalsIgnoreCase(best.getClassType())) {
            reasons.add("✔ Business class (more comfort)");
        } else if ("Economy".equalsIgnoreCase(best.getClassType())) {
            reasons.add("✔ Economy class (best value)");
        }

        if (reasons.isEmpty()) {
            reasons.add("✔ Best balanced option (price + availability)");
        }

        return new Result(best, reasons);
    }

    private double score(FlightRow f, double minPrice, int maxSeats) {

        double priceScore = f.getBasePrice() / minPrice;
        double seatScore = (double) maxSeats / f.getAvailableSeats();
        return priceScore + seatScore;
    }
}
