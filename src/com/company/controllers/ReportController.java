package com.company.controllers;

import com.company.controllers.interfaces.IReportController;
import com.company.repositories.ReportRepository;

public class ReportController implements IReportController {
    private final ReportRepository repo;

    public ReportController(ReportRepository repo) {
        this.repo = repo;
    }

    @Override
    public String revenueByAirline() {
        return repo.revenueByAirline();
    }

    @Override
    public String topRoutes() {
        return repo.topRoutes();
    }

    @Override
    public String revenueByHotelCity() {
        return repo.revenueByHotelCity();
    }

    @Override
    public String cancellationStats() {
        return repo.cancellationStats();
    }

    @Override
    public String averageStayByHotelCity() {
        return repo.averageStayByHotelCity();
    }
}
