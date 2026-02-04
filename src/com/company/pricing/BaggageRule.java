package com.company.pricing;

import com.company.models.ExtraSelection;
import com.company.models.FlightRow;
import com.company.models.HotelRow;

public class BaggageRule implements PriceRule {
    @Override
    public double apply(double currentTotal, double baseTotal,
                        FlightRow flight, HotelRow hotel, int nights,
                        int discountPercent, ExtraSelection extras,
                        StringBuilder breakdown) {

        if (extras != null && extras.getBaggageKg() > 0) {
            double add = extras.getBaggageKg() * 2.5;
            breakdown.append("• Baggage ").append(extras.getBaggageKg())
                    .append("kg: +").append(round2(add)).append("\n");
            return currentTotal + add;
        }
        breakdown.append("• Baggage: none\n");
        return currentTotal;
    }

    private double round2(double x) { return Math.round(x * 100.0) / 100.0; }
}
