package com.company.controllers;

import com.company.services.CancellationService;

public class CancellationController {
    private final CancellationService service;

    public CancellationController(CancellationService service) {
        this.service = service;
    }

    public String cancel(int bookingId) {
        return service.cancelBooking(bookingId);
    }
}