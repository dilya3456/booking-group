package com.company.pricing;

import com.company.models.ExtraSelection;
import com.company.models.FlightRow;
import com.company.models.HotelRow;

public class ClassMultiplierRule implements PriceRule {
    @Override
    public double apply(double currentTotal, double baseTotal,
                        FlightRow flight, HotelRow hotel, int nights,
                        int discountPercent, ExtraSelection extras,
                        StringBuilder breakdown) {

        String cls = flight.getClassType() == null ? "" : flight.getClassType().toUpperCase();
        if ("BUSINESS".equals(cls)) {
            double before = currentTotal;
            double after = currentTotal * 1.5;
            breakdown.append("• BUSINESS class x1.5: ")
                    .append(round2(before)).append(" -> ").append(round2(after)).append("\n");
            return after;
        }
        breakdown.append("• ECONOMY class: no multiplier\n");
        return currentTotal;
    }

    private double round2(double x) { return Math.round(x * 100.0) / 100.0; }
}