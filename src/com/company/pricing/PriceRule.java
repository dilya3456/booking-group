package com.company.pricing;

import com.company.models.ExtraSelection;
import com.company.models.FlightRow;
import com.company.models.HotelRow;

public interface PriceRule {

    double apply(double currentTotal,
                 double baseTotal,
                 FlightRow flight,
                 HotelRow hotel,
                 int nights,
                 int discountPercent,
                 ExtraSelection extras,
                 StringBuilder breakdown);
}
