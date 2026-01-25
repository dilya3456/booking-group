package com.company.controllers;

import com.company.repositories.interfaces.IRecommendationRepository;
import com.company.services.RecommendationService;

public class RecommendationController {

    private final IRecommendationRepository repo;
    private final RecommendationService service;

    public RecommendationController(IRecommendationRepository repo) {
        this.repo = repo;
        this.service = new RecommendationService();
    }

    public void showRecommendation() {
        var flights = repo.getAllFlights();
        var result = service.recommend(flights);

        if (result.best == null) {
            System.out.println("No recommendation available.");
            return;
        }

        System.out.println("=== Recommended flight ===");
        System.out.println("Flight ID: " + result.best.getId());
        System.out.println("Price: " + result.best.getBasePrice());
        System.out.println("Class: " + result.best.getClassType());
        System.out.println("Seats: " + result.best.getAvailableSeats());
        System.out.println("Why recommended:");
        result.reasons.forEach(System.out::println);
        System.out.println("--------------------------");
    }
}
