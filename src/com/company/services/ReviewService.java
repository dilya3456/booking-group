package com.company.services;

import com.company.repositories.interfaces.IReviewRepository;

import java.sql.Connection;

public class ReviewService {
    private final IReviewRepository repo;

    public ReviewService(IReviewRepository repo) {
        this.repo = repo;
    }

    public String addReview(int bookingId, int passengerId, int rating, String comment) {
        Connection con = null;

        try {
            if (rating < 1 || rating > 5) return "Rating must be 1..5.";
            if (comment != null && comment.length() > 500) return "Comment too long (max 500).";

            con = repo.getConnection();
            con.setAutoCommit(false);

            Integer hotelId = repo.getHotelIdByBooking(con, bookingId);
            if (hotelId == null) { con.rollback(); return "Booking not found."; }

            boolean ok = repo.canReview(con, bookingId, passengerId);
            if (!ok) { con.rollback(); return "You can review only your CONFIRMED booking."; }

            int reviewId = repo.insertReview(con, hotelId, passengerId, bookingId, rating, comment);

            repo.refreshHotelRating(con, hotelId);

            con.commit();
            return "Review added ✅ ID=" + reviewId + " | hotel_id=" + hotelId;

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ignore) {}
            return "Add review failed: " + e.getMessage();
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (Exception ignore) {}
            try { if (con != null) con.close(); } catch (Exception ignore) {}
        }
    }

    public String reviewsByHotel(int hotelId, int limit) {
        try { return repo.listReviewsByHotel(hotelId, limit); }
        catch (Exception e) { return "Error: " + e.getMessage(); }
    }

    public String myReviews(int passengerId, int limit) {
        try { return repo.listReviewsByPassenger(passengerId, limit); }
        catch (Exception e) { return "Error: " + e.getMessage(); }
    }
}
