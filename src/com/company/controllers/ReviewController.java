package com.company.controllers;

import com.company.controllers.interfaces.IReviewController;
import com.company.services.ReviewService;

public class ReviewController implements IReviewController {
    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @Override
    public String addReview(int bookingId, int passengerId, int rating, String comment) {
        return service.addReview(bookingId, passengerId, rating, comment);
    }

    @Override
    public String reviewsByHotel(int hotelId, int limit) {
        return service.reviewsByHotel(hotelId, limit);
    }

    @Override
    public String myReviews(int passengerId, int limit) {
        return service.myReviews(passengerId, limit);
    }
}