package com.company.pricing;

import com.company.models.ExtraSelection;
import com.company.models.FlightRow;
import com.company.models.HotelRow;

public class InsuranceRule implements PriceRule {
    @Override
    public double apply(double currentTotal, double baseTotal,
                        FlightRow flight, HotelRow hotel, int nights,
                        int discountPercent, ExtraSelection extras,
                        StringBuilder breakdown) {

        if (extras != null && extras.isInsurance()) {
            double add = baseTotal * 0.03;
            double after = currentTotal + add;
            breakdown.append("• Insurance +3% base (").append(round2(add)).append(")\n");
            return after;
        }
        breakdown.append("• Insurance: not selected\n");
        return currentTotal;
    }

    private double round2(double x) { return Math.round(x * 100.0) / 100.0; }
}