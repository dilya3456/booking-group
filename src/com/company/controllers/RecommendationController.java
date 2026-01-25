package com.company.controllers;

import com.company.controllers.interfaces.IRecommendationController;
import com.company.repositories.interfaces.IRecommendationRepository;
import com.company.services.RecommendationService;

public class RecommendationController implements IRecommendationController {

    private final IRecommendationRepository repo;
    private final RecommendationService service;

    public RecommendationController(IRecommendationRepository repo) {
        this.repo = repo;
        this.service = new RecommendationService();
    }

    @Override
    public void showRecommendation() {
        var flights = repo.getAvailableFlights();
        var result = service.recommend(flights);

        if (result.getBest() == null) {
            System.out.println("No recommendation available.");
            return;
        }

        var f = result.getBest();

        System.out.println("=== Recommended flight ===");
        System.out.println("Flight ID: " + f.getId());
        System.out.println("Price: " + (int) f.getBasePrice() + " KZT");
        System.out.println("Class: " + f.getClassType());
        System.out.println("Available seats: " + f.getAvailableSeats());
        System.out.println();
        System.out.println("Why recommended:");
        for (String r : result.getReasons()) {
            System.out.println(r);
        }
        System.out.println("--------------------------");
    }
}
