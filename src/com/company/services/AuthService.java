package com.company.services;

import com.company.repositories.interfaces.IAuthRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class AuthService {
    private final IAuthRepository repo;

    public AuthService(IAuthRepository repo) {
        this.repo = repo;
    }

    public String register(String username, String password) {
        username = safe(username);
        password = safe(password);

        if (username.length() < 3) return "Username must be at least 3 characters.";
        if (password.length() < 4) return "Password must be at least 4 characters.";

        if (repo.usernameExists(username)) return "Username already exists.";

        String hash = sha256(username + ":" + password);
        boolean ok = repo.register(username, hash);

        return ok ? "Registered successfully." : "Registration failed.";
    }

    public Integer login(String username, String password) {
        username = safe(username);
        password = safe(password);

        String hash = sha256(username + ":" + password);
        return repo.login(username, hash);
    }

    private String safe(String s) {
        if (s == null) return "";
        return s.trim();
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hash error: " + e.getMessage());
        }
    }
    public String getRoleByUsername(String username) {
        return repo.getRoleByUsername(username);
    }

}
