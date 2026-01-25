package com.company.repositories;

import com.company.data.interfaces.IDB;
import com.company.repositories.interfaces.IAuthRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthRepository implements IAuthRepository {
    private final IDB db;

    public AuthRepository(IDB db) {
        this.db = db;
    }

    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM auth_users WHERE username = ? LIMIT 1";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            throw new RuntimeException("usernameExists error: " + e.getMessage());
        }
    }

    @Override
    public boolean register(String username, String passwordHash) {
        String sql = "INSERT INTO auth_users(username, password_hash) VALUES (?, ?)";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Integer login(String username, String passwordHash) {
        String sql = "SELECT id FROM auth_users WHERE username = ? AND password_hash = ? LIMIT 1";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
            return null;

        } catch (Exception e) {
            throw new RuntimeException("login error: " + e.getMessage());
        }
    }
}
