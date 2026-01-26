package com.company.services;

import com.company.models.ExtraSelection;
import com.company.models.FlightRow;
import com.company.models.HotelRow;
import com.company.repositories.interfaces.IBookingRepository;

import java.sql.Connection;
import java.util.List;

public class BookingService {
    private final IBookingRepository repo;
    private final PriceCalculatorService priceCalc;

    public BookingService(IBookingRepository repo, PriceCalculatorService priceCalc) {
        this.repo = repo;
        this.priceCalc = priceCalc;
    }



    public String createBooking(int passengerId, int flightId, int hotelId, int nights,
                                String paymentMethod, Integer createdByUserId, ExtraSelection extras) {
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

            double baseTotal = priceCalc.calculateTotal(
                    flight.getBasePrice(),
                    hotel.getPricePerNight(),
                    nights,
                    discount,
                    hotel.getStars(),
                    flight.getClassType()
            );

            if (extras == null) extras = ExtraSelection.basic();

            double extrasTotal = calcExtrasTotal(baseTotal, extras);
            double total = baseTotal + extrasTotal;

            int bookingId = repo.insertBooking(con, passengerId, flightId, hotelId, nights, total, createdByUserId);

            repo.insertBookingExtras(con, bookingId, extras, extrasTotal);

            repo.insertPayment(con, bookingId, total, paymentMethod.toUpperCase());
            repo.insertHistory(con, bookingId, "CREATED",
                    "Method=" + paymentMethod.toUpperCase() + ", base=" + round2(baseTotal) + ", extras=" + round2(extrasTotal));

            repo.decreaseSeat(con, flightId);
            repo.decreaseRoom(con, hotelId);

            con.commit();

            return "Booking CONFIRMED ✅ ID=" + bookingId
                    + " | base=" + round2(baseTotal)
                    + " | extras=" + round2(extrasTotal)
                    + " | total=" + round2(total)
                    + " | discount=" + discount + "%";

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ignore) {}
            return "Create booking failed: " + e.getMessage();
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (Exception ignore) {}
        }
    }



    public int createGroupBooking(List<Integer> passengerIds, int flightId, int hotelId, int nights,
                                  String method, Integer createdByUserId, ExtraSelection extras) {
        Connection con = null;
        try {
            if (nights < 1 || nights > 30) throw new RuntimeException("Nights must be 1..30.");
            if (!isValidMethod(method)) throw new RuntimeException("Payment method must be CARD/CASH/TRANSFER.");
            if (extras == null) extras = ExtraSelection.basic();

            con = repo.getConnection();
            con.setAutoCommit(false);

            int bookingId = repo.createGroupBooking(con, passengerIds, flightId, hotelId, nights, method, createdByUserId);

            double extrasTotal = 0.0; // потом посчитаешь нормально
            repo.insertBookingExtras(con, bookingId, extras, extrasTotal);
            repo.insertHistory(con, bookingId, "EXTRAS_ADDED", "Group extras added");

            con.commit();
            return bookingId;

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ignore) {}
            throw new RuntimeException("Create group booking failed: " + e.getMessage(), e);
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (Exception ignore) {}
            try { if (con != null) con.close(); } catch (Exception ignore) {}   // <-- ДОБАВЬ close()
        }
    }



    public int createPassenger(String name, String surname, String gender, int age, String passportNumber) {
        try {
            boolean male = gender != null && gender.equalsIgnoreCase("male");
            return repo.createPassenger(name, surname, male, age, passportNumber);
        } catch (Exception e) {
            throw new RuntimeException("Create passenger failed: " + e.getMessage());
        }
    }


    public String listFlights(int limit) {
        try { return repo.listFlights(limit); }
        catch (Exception e) { return "Error: " + e.getMessage(); }
    }

    public String listHotels(int limit) {
        try { return repo.listHotels(limit); }
        catch (Exception e) { return "Error: " + e.getMessage(); }
    }

    public String listPassengers(int limit) {
        try { return repo.listPassengers(limit); }
        catch (Exception e) { return "Error: " + e.getMessage(); }
    }

    public String getBookingDetails(int bookingId) {
        try { return repo.getBookingDetails(bookingId); }
        catch (Exception e) { return "Error: " + e.getMessage(); }
    }

    // ================= SEATS =================

    public String getSeatMap(int flightId) {
        try { return repo.getSeatMap(flightId); }
        catch (Exception e) { return "Error: " + e.getMessage(); }
    }

    public String chooseSeats(int bookingId, int flightId, List<String> seatCodes) {
        Connection con = null;
        try {
            if (seatCodes == null || seatCodes.isEmpty()) return "No seats selected.";

            con = repo.getConnection();
            con.setAutoCommit(false);

            boolean free = repo.areSeatsFree(con, flightId, seatCodes);
            if (!free) {
                con.rollback();
                return "Some seats are already occupied.";
            }

            repo.occupySeats(con, bookingId, flightId, seatCodes);
            repo.insertHistory(con, bookingId, "SEATS_CHOSEN", "Seats=" + seatCodes);

            con.commit();
            return "Seats saved ✅ " + seatCodes;

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ignore) {}
            return "Choose seats failed: " + e.getMessage();
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (Exception ignore) {}
        }
    }



    private boolean isValidMethod(String m) {
        if (m == null) return false;
        String x = m.toUpperCase();
        return x.equals("CARD") || x.equals("CASH") || x.equals("TRANSFER");
    }

    private double calcExtrasTotal(double baseTotal, ExtraSelection extras) {
        double sum = 0.0;

        if (extras.getBaggageKg() > 0) sum += extras.getBaggageKg() * 2.5;

        switch (extras.getMealType()) {
            case STANDARD -> sum += 12.0;
            case VEG -> sum += 13.0;
            case KIDS -> sum += 10.0;
            default -> { }
        }

        if (extras.isPriority()) sum += 20.0;
        if (extras.isInsurance()) sum += baseTotal * 0.03;

        return sum;
    }

    private double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
}
