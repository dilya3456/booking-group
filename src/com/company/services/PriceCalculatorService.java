package com.company.services;

public class PriceCalculatorService {

    public double calculateTotal(double flightBasePrice, double hotelPricePerNight, int nights,
                                 int discountPercent, int hotelStars, String classType) {

        double flight = flightBasePrice;


        if ("BUSINESS".equalsIgnoreCase(classType)) {
            flight *= 1.25;
        }

        double hotel = hotelPricePerNight * nights;


        if (hotelStars == 5) {
            hotel *= 1.20;
        }

        double total = flight + hotel;


        if (discountPercent > 0) {
            total = total * (1.0 - (discountPercent / 100.0));
        }

        return round2(total);
    }

    private double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
}
