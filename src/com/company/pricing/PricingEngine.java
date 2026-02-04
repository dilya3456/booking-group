package com.company.pricing;

import com.company.models.ExtraSelection;
import com.company.models.FlightRow;
import com.company.models.HotelRow;

import java.util.List;

public class PricingEngine {
    private final List<PriceRule> rules;

    public PricingEngine(List<PriceRule> rules) {
        this.rules = rules;
    }

    public PricingResult calculate(double baseTotal,
                                   FlightRow flight,
                                   HotelRow hotel,
                                   int nights,
                                   int discountPercent,
                                   ExtraSelection extras) {

        StringBuilder breakdown = new StringBuilder();
        breakdown.append("Base total: ").append(round2(baseTotal)).append("\n");

        double total = baseTotal;
        for (PriceRule rule : rules) {
            total = rule.apply(total, baseTotal, flight, hotel, nights, discountPercent, extras, breakdown);
        }

        breakdown.append("TOTAL: ").append(round2(total)).append("\n");
        return new PricingResult(baseTotal, total, breakdown.toString());
    }

    private double round2(double x) { return Math.round(x * 100.0) / 100.0; }
}