package com.company.controllers;

import com.company.controllers.interfaces.IPromoController;
import com.company.services.PromoService;

public class PromoController implements IPromoController {
    private final PromoService service;

    public PromoController(PromoService service) {
        this.service = service;
    }

    @Override
    public String applyPromo(int bookingId, String code) {
        return service.applyPromo(bookingId, code);
    }
}
