package com.company.controllers.interfaces;

public interface IReviewController {
    String addReview(int bookingId, int passengerId, int rating, String comment);
    String reviewsByHotel(int hotelId, int limit);
    String myReviews(int passengerId, int limit);
}
