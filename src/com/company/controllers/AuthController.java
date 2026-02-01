package com.company.controllers;

import com.company.services.AuthService;

public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    public Integer login(String username, String password) {
        return service.login(username, password);
    }

    public String register(String username, String password) {
        return service.register(username, password);
    }
    public String getRoleByUsername(String username) {
        return service.getRoleByUsername(username);
    }

}
