package com.company.services;

import com.company.models.FlightRow;
import com.company.models.HotelRow;
import com.company.repositories.interfaces.IBookingRepository;

import java.sql.Connection;

public class BookingService {
    private final IBookingRepository repo;
    private final PriceCalculatorService priceCalc;

    public BookingService(IBookingRepository repo, PriceCalculatorService priceCalc) {
        this.repo = repo;
        this.priceCalc = priceCalc;
    }

    public String createBooking(int passengerId, int flightId, int hotelId, int nights, String paymentMethod, Integer createdByUserId) {
        Connection con = null;

        try {
            if (nights < 1 || nights > 30) return "Nights must be 1..30.";
            if (!isValidMethod(paymentMethod)) return "Payment method must be CARD/CASH/TRANSFER.";

            con = repo.getConnection();
            con.setAutoCommit(false);

            if (!repo.passengerExists(con, passengerId)) {
                con.rollback();
                return "Passenger not found.";
            }

            FlightRow flight = repo.getFlightForUpdate(con, flightId);
            if (flight == null) { con.rollback(); return "Flight not found."; }
            if (flight.getAvailableSeats() <= 0) { con.rollback(); return "No seats available."; }

            HotelRow hotel = repo.getHotelForUpdate(con, hotelId);
            if (hotel == null) { con.rollback(); return "Hotel not found."; }
            if (hotel.getAvailableRooms() <= 0) { con.rollback(); return "No rooms available."; }

            int discount = repo.getPassengerDiscount(con, passengerId);

            double total = priceCalc.calculateTotal(
                    flight.getBasePrice(),
                    hotel.getPricePerNight(),
                    nights,
                    discount,
                    hotel.getStars(),
                    flight.getClassType()
            );

            int bookingId = repo.insertBooking(con, passengerId, flightId, hotelId, nights, total, createdByUserId);

            repo.insertPayment(con, bookingId, total, paymentMethod.toUpperCase());
            repo.insertHistory(con, bookingId, "CREATED", "Auto-confirmed. Method=" + paymentMethod.toUpperCase());

            repo.decreaseSeat(con, flightId);
            repo.decreaseRoom(con, hotelId);

            con.commit();

            return "Booking CONFIRMED ✅ ID=" + bookingId
                    + " | total=" + total
                    + " | discount=" + discount + "%";

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ignore) {}
            return "Create booking failed: " + e.getMessage();
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (Exception ignore) {}
        }
    }

    public String listFlights(int limit) {
        try {
            return repo.listFlights(limit);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String listHotels(int limit) {
        try {
            return repo.listHotels(limit);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String listPassengers(int limit) {
        try {
            return repo.listPassengers(limit);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String getBookingDetails(int bookingId) {
        try {
            return repo.getBookingDetails(bookingId);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private boolean isValidMethod(String m) {
        if (m == null) return false;
        String x = m.toUpperCase();
        return x.equals("CARD") || x.equals("CASH") || x.equals("TRANSFER");
    }
}
