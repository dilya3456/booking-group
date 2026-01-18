package com.company.controllers;

import com.company.repositories.ReportRepository;

public class ReportController {
    private final ReportRepository repo;

    public ReportController(ReportRepository repo) {
        this.repo = repo;
    }

    public String revenueByAirline() {
        return repo.revenueByAirline();
    }

    public String topRoutes() {
        return repo.topRoutes();
    }

    public String revenueByHotelCity() {
        return repo.revenueByHotelCity();
    }

    public String cancellationStats() {
        return repo.cancellationStats();
    }

    public String averageStayByHotelCity() {
        return repo.averageStayByHotelCity();
    }
}