package com.company.controllers;

import com.company.controllers.interfaces.ICancellationController;
import com.company.services.CancellationService;

public class CancellationController implements ICancellationController {
    private final CancellationService service;

    public CancellationController(CancellationService service) {
        this.service = service;
    }

    @Override
    public String cancel(int bookingId) {
        return service.cancelBooking(bookingId);
    }
}